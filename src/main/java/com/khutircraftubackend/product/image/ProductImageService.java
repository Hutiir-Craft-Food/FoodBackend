package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.exception.ImageNotFoundException;
import com.khutircraftubackend.product.image.exception.ImagesCountMismatchException;
import com.khutircraftubackend.product.image.exception.PositionAlreadyExistsException;
import com.khutircraftubackend.product.image.exception.TooManyImagesException;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.validated.ImageMimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private static final Long MAX_COUNT_FILES = 5L;
    private static final int TEMP_OFFSET = 100;

    @Value("${allowed.mime.types}")
    private Set<String> allowedMimeTypes;

    @Transactional
    public ProductImageResponse createImages(Long productId, ProductImageUploadRequest request,
                                              List<MultipartFile> imageFiles) {

        if (imageFiles.size() > MAX_COUNT_FILES) {
            String errorMessage = String.format(
                    ProductImageResponseMessages.ERROR_TOO_MANY_IMAGES,
                    MAX_COUNT_FILES);
            throw new TooManyImagesException(errorMessage);
        }

        if (imageFiles.size() != request.images().size()) {
            String errorMessage = String.format(
                    ProductImageResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                    imageFiles.size(),
                    request.images().size());
            throw new ImagesCountMismatchException(errorMessage);
        }

        mimeValidator.validateMimeTypes(imageFiles, allowedMimeTypes);

        if (hasAnyDuplicatePosition(productId, request)) {
            throw new PositionAlreadyExistsException(ProductImageResponseMessages.ERROR_POSITION_ALREADY_EXISTS);
        }

        ProductEntity product = ensureProductExists(productId);
        List<ProductImageEntity> allImagesEntities = new ArrayList<>();

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile imageFile = imageFiles.get(i);
            ProductImageUploadRequest.Image imageMeta = request.images().get(i);
            int position = imageMeta.position();
            ProductImageEntity uploadedImageEntities = uploadProductImageFile(product, imageFile, position);
            allImagesEntities.add(uploadedImageEntities);
        }

        List<ProductImageEntity> savedImageEntities = imageRepository.saveAll(allImagesEntities);

        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(savedImageEntities))
                .build();
    }

    private ProductEntity ensureProductExists(Long productId) {
        return productService.findProductById(productId);
    }

    private ProductImageEntity uploadProductImageFile(ProductEntity product, MultipartFile imageFile, int position) {
        List<ImageSize> sizes = List.of(ImageSize.THUMBNAIL, ImageSize.SMALL, ImageSize.MEDIUM, ImageSize.LARGE);

        String originalFileName = imageFile.getOriginalFilename();
        List<ProductImageVariant> imageVariant = new ArrayList<>();
        ProductImageEntity imageEntity = ProductImageEntity.builder()
                .product(product)
                .position(position)
                .build();

        for (ImageSize imageSize : sizes) {
            try {
                byte[] bytes = imageFile.getBytes();
                String link = storageService.upload(bytes, originalFileName);
                ProductImageVariant imageVariantEntity = ProductImageVariant.builder()
                        .image(imageEntity)
                        .tsSize(imageSize)
                        .link(link)
                        .build();

                imageVariant.add(imageVariantEntity);

            } catch (IOException e) {
                // TODO: review exception handling here:
                throw new RuntimeException(e);
            }
        }

        imageEntity.setVariants(imageVariant);
        return imageEntity;
    }

    private boolean hasAnyDuplicatePosition(Long productId, ProductImageUploadRequest request) {
        Set<Integer> existingPositions = imageRepository.findByProductId(productId).stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        return request.images().stream()
                .map(ProductImageUploadRequest.Image::position)
                .anyMatch(existingPositions::contains);
    }


    @Transactional
    public ProductImageResponse updateImages(Long productId, ProductImageChangeRequest request) {

        ensureProductExists(productId);

        List<ProductImageChangeRequest.Image> requestImages = request.images();
        List<ProductImageEntity> allImages = imageRepository.findByProductId(productId);
        int totalCountImages = requestImages.size() * ImageSize.values().length;

        if (allImages.size() != totalCountImages) {
            throw new ImagesCountMismatchException(
                    String.format(ProductImageResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                            totalCountImages / ImageSize.values().length,
                            allImages.size() / ImageSize.values().length));
        }

        validateAllImagesExistByUid(requestImages, allImages);

        Map<Long, List<ProductImageEntity>> imagesById = allImages.stream()
                .collect(Collectors.groupingBy(ProductImageEntity::getId));

        Map<Integer, Long> currentPositions = new HashMap<>();
        for (ProductImageEntity image : allImages) {
            currentPositions.put(image.getPosition(), image.getId());
        }

        Set<ProductImageEntity> toSave = new HashSet<>();
        resolvePositionConflicts(requestImages, imagesById, currentPositions, toSave);
        assignNewPositions(requestImages, imagesById, currentPositions, toSave);
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

            List<ProductImageEntity> group = imagesById.get(id);
            if (group == null) continue;

            Long occupyingUid = currentPositions.get(newPosition);
            if (occupyingUid != null && !occupyingUid.equals(id)) {
                List<ProductImageEntity> conflictGroup = imagesById.get(occupyingUid);
                for (ProductImageEntity conflictImage : conflictGroup) {
                    int oldPosition = conflictImage.getPosition();
                    int shiftedPosition = oldPosition + TEMP_OFFSET;

                    conflictImage.setPosition(shiftedPosition);
                    toSave.add(conflictImage);

                    currentPositions.remove(oldPosition);
                    currentPositions.put(shiftedPosition, conflictImage.getId());
                }
            }
        }
    }

    private void assignNewPositions(List<ProductImageChangeRequest.Image> requestImages,
                                    Map<Long, List<ProductImageEntity>> imagesByUid,
                                    Map<Integer, Long> currentPositions,
                                    Set<ProductImageEntity> toSave) {
        for (ProductImageChangeRequest.Image requestImage : requestImages) {
            Long id = requestImage.id();
            int newPosition = requestImage.position();

            List<ProductImageEntity> group = imagesByUid.get(id);
            if (group == null) continue;

            for (ProductImageEntity image : group) {
                image.setPosition(newPosition);
                toSave.add(image);
            }

            currentPositions.put(newPosition, id);
        }
    }

    private void validateAllImagesExistByUid(List<ProductImageChangeRequest.Image> imagesFromRequest,
                                             List<ProductImageEntity> allImages) {
        Set<Long> dbUid = allImages.stream()
                .map(ProductImageEntity::getId)
                .collect(Collectors.toSet());

        Set<Long> missingUid = imagesFromRequest.stream()
                .map(ProductImageChangeRequest.Image::id)
                .filter(uid -> !dbUid.contains(uid))
                .collect(Collectors.toSet());

        if (!missingUid.isEmpty()) {
            throw new ImageNotFoundException(String.format(ProductImageResponseMessages.ERROR_IMAGE_NOT_FOUND_BY_ID, missingUid));
        }
    }

    @Transactional(readOnly = true)
    public ProductImageResponse getProductImages(Long productId) {

        ensureProductExists(productId);

        List<ProductImageEntity> entities = imageRepository.findByProductId(productId);

        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(entities))
                .build();
    }

    @Transactional
    public void deleteProductImages(Long productId, List<Integer> positionIds) {

        ensureProductExists(productId);

        List<ProductImageEntity> entities = imageRepository.findByProductId(productId);

        List<ProductImageEntity> toDelete = (positionIds == null || positionIds.isEmpty())
                ? entities
                : entities.stream()
                .filter(e -> positionIds.contains(e.getPosition()))
                .toList();

        toDelete.forEach(this::safeDeleteFromStorage);

        imageRepository.deleteAll(toDelete);
    }

    private void safeDeleteFromStorage(ProductImageEntity entity) {
        List<ProductImageVariant> imageVariantList = entity.getVariants();
        try {
            for (ProductImageVariant variant : imageVariantList) {
                String imageUrl = variant.getLink();
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    storageService.deleteByUrl(imageUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO need to implement SCRUM-211.
            // Need implement global CloudStorageException??
            // You can insert the team lead's resolution here
        }
    }
}