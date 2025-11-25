package com.khutircraftubackend.product.image;

import com.khutircraftubackend.exception.FileReadingException;
import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.exception.*;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import com.khutircraftubackend.storage.StorageResponseMessage;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.storage.exception.StorageException;
import com.khutircraftubackend.validated.ImageMimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository imageRepository;
    private final ProductService productService;
    private final ProductImageMapper imageMapper;
    private final StorageService storageService;
    private final ImageMimeValidator mimeValidator;
    private static final Long MAX_UPLOAD_FILE_COUNT = 5L;
    private static final int TEMP_POSITION_OFFSET = 100;

    @Value("${allowed.mime.types}")
    private Set<String> allowedMimeTypes;

    @Transactional()
    public ProductImageResponse uploadImages(Long productId, ProductImageUploadRequest request,
                                             List<MultipartFile> files) {

        validateUploadRequest(productId, request, files);
        ProductEntity product = ensureProductExists(productId);
        List<ProductImageEntity> createdImages = createImagesInternal(product, request, files);
        List<ProductImageEntity> saved = imageRepository.saveAll(createdImages);

        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(saved))
                .build();
    }

    private void validateUploadRequest(Long productId, ProductImageUploadRequest request, List<MultipartFile> files) {
        if (files.size() > MAX_UPLOAD_FILE_COUNT) {
            throw new TooManyImagesException(String.format(
                    ProductImageResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_UPLOAD_FILE_COUNT));
        }

        if (files.size() != request.images().size()) {
            throw new ImagesCountMismatchException(String.format(
                    ProductImageResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                    files.size(),
                    request.images().size()));
        }

        mimeValidator.validateMimeTypes(files, allowedMimeTypes);

        if (hasAnyDuplicatePosition(productId, request)) {
            throw new PositionAlreadyExistsException(ProductImageResponseMessages.ERROR_POSITION_ALREADY_EXISTS);
        }
    }

    private ProductEntity ensureProductExists(Long productId) {
        return productService.findProductById(productId);
    }

    private List<ProductImageEntity> createImagesInternal(ProductEntity product,
                                                          ProductImageUploadRequest meta,
                                                          List<MultipartFile> files) {
        List<ProductImageEntity> entities = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            int position = meta.images().get(i).position();
            entities.add(createImageForAllSizes(product, file, position));
        }

        return entities;
    }

    private ProductImageEntity createImageForAllSizes(ProductEntity product,
                                                      MultipartFile file,
                                                      int position) {
        ProductImageEntity image = ProductImageEntity.builder()
                .product(product)
                .position(position)
                .build();

        List<ProductImageVariant> variants = new ArrayList<>();

        for (ImageSize size : ImageSize.values()) {
            // TODO [SCRUM-210] need to implement for image resizing
            //  result of resized image will be array of bytes.
            //  example of java dependency for image resizing: net.coobird
            String url;
            try {
                url = storageService.upload(file);
            } catch (FileReadingException ex) {
                throw new StorageException(StorageResponseMessage.ERROR_SAVE);
            }

            ProductImageVariant variant = ProductImageVariant.builder()
                    .image(image)
                    .tsSize(size)
                    .link(url)
                    .build();


            variants.add(variant);
        }

        image.setVariants(variants);
        return image;
    }

    private boolean hasAnyDuplicatePosition(Long productId, ProductImageUploadRequest request) {
        Set<Integer> existingPositions = imageRepository.findByProductId(productId).stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        return request.images().stream()
                .map(ProductImageUploadRequest.ImageUpload::position)
                .anyMatch(existingPositions::contains);
    }

    @Transactional()
    public ProductImageResponse reorderImages(Long productId, ProductImageChangeRequest request) {

        ensureProductExists(productId);

        List<ProductImageEntity> allImages = imageRepository.findByProductId(productId);
        validateImageIds(request, allImages);

        if (allImages.size() != request.images().size()) {
            throw new ImagesCountMismatchException(String.format(
                    ProductImageResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                    request.images().size(),
                    allImages.size()));
        }

        Map<Long, List<ProductImageEntity>> groupById = allImages.stream()
                .collect(Collectors.groupingBy(ProductImageEntity::getId));

        Map<Integer, Long> positionToId = allImages.stream()
                .collect(Collectors.toMap(ProductImageEntity::getPosition,
                        ProductImageEntity::getId));

        Set<ProductImageEntity> toSave = new HashSet<>();
        resolvePositionConflicts(request.images(), groupById, positionToId, toSave);
        applyNewPositions(request.images(), groupById, positionToId, toSave);
        List<ProductImageEntity> updatedImages = imageRepository.saveAll(toSave);

        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(updatedImages))
                .build();
    }

    private void resolvePositionConflicts(List<ProductImageChangeRequest.Image> requestImages,
                                          Map<Long, List<ProductImageEntity>> imagesById,
                                          Map<Integer, Long> currentPositions,
                                          Set<ProductImageEntity> toSave) {
        for (ProductImageChangeRequest.Image requestImage : requestImages) {
            Long id = requestImage.id();
            int newPosition = requestImage.position();
            Long conflictId = currentPositions.get(newPosition);
            if (conflictId != null && !conflictId.equals(id)) {
                for (ProductImageEntity e : imagesById.get(conflictId)) {
                    int oldPos = e.getPosition();
                    int tempPos = oldPos + TEMP_POSITION_OFFSET;
                    e.setPosition(tempPos);
                    currentPositions.remove(oldPos);
                    currentPositions.put(tempPos, e.getId());

                    toSave.add(e);
                }
            }
        }
    }

    private void applyNewPositions(List<ProductImageChangeRequest.Image> requestImages,
                                   Map<Long, List<ProductImageEntity>> grouped,
                                   Map<Integer, Long> currentPositions,
                                   Set<ProductImageEntity> toSave) {
        for (ProductImageChangeRequest.Image requestImage : requestImages) {
            Long id = requestImage.id();
            int newPosition = requestImage.position();

            List<ProductImageEntity> list = grouped.get(id);
            if (list == null) continue;

            for (ProductImageEntity e : list) {
                int oldPos = e.getPosition();
                e.setPosition(newPosition);
                toSave.add(e);

                currentPositions.remove(oldPos);
                currentPositions.put(newPosition, id);
            }
        }
    }

    private void validateImageIds(ProductImageChangeRequest request,
                                  List<ProductImageEntity> dbImages) {
        Set<Long> dbIds = dbImages.stream().map(ProductImageEntity::getId).collect(Collectors.toSet());
        Set<Long> missing = request.images().stream()
                .map(ProductImageChangeRequest.Image::id)
                .filter(id -> !dbIds.contains(id))
                .collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            throw new ImageNotFoundException(
                    String.format(ProductImageResponseMessages.ERROR_IMAGE_NOT_FOUND_BY_ID, missing));
        }
    }

    @Transactional(readOnly = true)
    public ProductImageResponse getImages(Long productId) {
        ensureProductExists(productId);
        List<ProductImageEntity> list = imageRepository.findByProductId(productId);

        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(list))
                .build();
    }

    @Transactional()
    public void deleteImages(Long productId, List<Integer> positions) {

        ensureProductExists(productId);

        List<ProductImageEntity> all = imageRepository.findByProductId(productId);
        validatePositions(positions, all);
        List<ProductImageEntity> target = (positions.isEmpty())
                ? all
                : all.stream()
                .filter(e -> positions.contains(e.getPosition()))
                .toList();

        target.forEach(this::safeDeleteFromStorage);

        imageRepository.deleteAll(target);
    }

    private void safeDeleteFromStorage(ProductImageEntity image) {
        if (image.getVariants() == null) return;

        image.getVariants().forEach(variant ->
                storageService.deleteByUrl(variant.getLink())
        );
    }

    public void validatePositions(List<Integer> positions, List<ProductImageEntity> images) {

        Set<Integer> validPositions = Set.of(0, 1, 2, 3, 4);
        Set<Integer> invalid = positions.stream()
                .filter(p -> !validPositions.contains(p))
                .collect(Collectors.toSet());

        if (!invalid.isEmpty()) {
            throw new ImageValidationException(
                    String.format(ProductImageResponseMessages.ERROR_INVALID_POSITION, invalid));
        }

        Set<Integer> existingPositions = images.stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        Set<Integer> notFound = positions.stream()
                .filter(p -> !existingPositions.contains(p))
                .collect(Collectors.toSet());

        if (!notFound.isEmpty()) {
            throw new ImageNotFoundException(
                    String.format(ProductImageResponseMessages.ERROR_NOT_FOUND_POSITION, notFound));
        }
    }
}