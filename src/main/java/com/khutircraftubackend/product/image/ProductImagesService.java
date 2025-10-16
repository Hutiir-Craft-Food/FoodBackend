package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.exception.ImageNotFoundException;
import com.khutircraftubackend.product.image.exception.ImagesCountMismatchException;
import com.khutircraftubackend.product.image.exception.PositionAlreadyExistsException;
import com.khutircraftubackend.product.image.exception.TooManyImagesException;
import com.khutircraftubackend.product.image.request.ProductImagesChangeRequest;
import com.khutircraftubackend.product.image.request.ProductImagesUploadRequest;
import com.khutircraftubackend.product.image.response.ProductImagesResponse;
import com.khutircraftubackend.product.image.response.ProductImagesResponseMessages;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.validated.ImageMimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImagesService {

    private final ProductImageRepository imageRepository;
    private final ProductService productService;
    private final ProductImagesMapper imageMapper;
    private final StorageService storageService;
    private final ImageMimeValidator mimeValidator;
    private static final Long MAX_COUNT_FILES = 5L;
    private static final int TEMP_OFFSET = 100;

    @Value("${allowed.mime.types}")
    private Set<String> allowedMimeTypes;

    @Transactional
    public ProductImagesResponse createImages(Long productId, ProductImagesUploadRequest request,
                                              List<MultipartFile> imageFiles) {

        if (imageFiles.size() > MAX_COUNT_FILES) {
            String errorMessage = String.format(
                    ProductImagesResponseMessages.ERROR_TOO_MANY_IMAGES,
                    MAX_COUNT_FILES);
            throw new TooManyImagesException(errorMessage);
        }

        if (imageFiles.size() != request.images().size()) {
            String errorMessage = String.format(
                    ProductImagesResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                    imageFiles.size(),
                    request.images().size());
            throw new ImagesCountMismatchException(errorMessage);
        }

        mimeValidator.validateMimeTypes(imageFiles, allowedMimeTypes);

        if (hasAnyDuplicatePosition(productId, request)) {
            throw new PositionAlreadyExistsException(ProductImagesResponseMessages.ERROR_POSITION_ALREADY_EXISTS);
        }

        ProductEntity product = ensureProductExists(productId);
        List<ProductImageEntity> allImagesEntities = new LinkedList<>();

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile imageFile = imageFiles.get(i);
            ProductImagesUploadRequest.Image imageMeta = request.images().get(i);
            int position = imageMeta.position();
            List<ProductImageEntity> uploadedImageEntities = uploadProductImageFile(product, imageFile, position);
            allImagesEntities.addAll(uploadedImageEntities);
        }

        List<ProductImageEntity> savedImageEntities = imageRepository.saveAll(allImagesEntities);

        return ProductImagesResponse.builder()
                .images(imageMapper.toProductImageDto(savedImageEntities))
                .build();
    }

    private ProductEntity ensureProductExists(Long productId) {
        return productService.findProductById(productId);
    }

    private List<ProductImageEntity> uploadProductImageFile(ProductEntity product, MultipartFile imageFile, int position) {
        List<ImageSize> sizes = List.of(ImageSize.THUMBNAIL, ImageSize.SMALL, ImageSize.MEDIUM, ImageSize.LARGE);

        String uid = UUID.randomUUID().toString();
        String originalFileName = imageFile.getOriginalFilename();
        List<ProductImageEntity> imageEntities = new LinkedList<>();

        for (ImageSize imageSize : sizes) {
            try {
                // TODO need to implement SCRUM-210 for image resizing
                //  result of resized image will be array of bytes.
                //  example of java dependency for image resizing: net.coobird
                byte[] bytes = imageFile.getBytes();
                String link = storageService.upload(bytes, originalFileName);
                ProductImageEntity imageEntity = ProductImageEntity.builder()
                        .product(product)
                        .uid(uid)
                        .position(position)
                        .tsSize(imageSize)
                        .link(link)
                        .build();

                imageEntities.add(imageEntity);

            } catch (IOException e) {
                // TODO: review exception handling here:
                throw new RuntimeException(e);
            }
        }

        return imageEntities;
    }

    private boolean hasAnyDuplicatePosition(Long productId, ProductImagesUploadRequest request) {
        Set<Integer> existingPositions = imageRepository.findByProductId(productId).stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        return request.images().stream()
                .map(ProductImagesUploadRequest.Image::position)
                .anyMatch(existingPositions::contains);
    }


    @Transactional
    public ProductImagesResponse updateImages(Long productId, ProductImagesChangeRequest request) {

        ensureProductExists(productId);

        List<ProductImagesChangeRequest.Image> requestImages = request.images();
        List<ProductImageEntity> allImages = imageRepository.findByProductId(productId);
        int totalCountImages = requestImages.size() * ImageSize.values().length;

        if (allImages.size() != totalCountImages) {
            throw new ImagesCountMismatchException(
                    String.format(ProductImagesResponseMessages.ERROR_IMAGES_COUNT_MISMATCH,
                            totalCountImages / ImageSize.values().length,
                            allImages.size() / ImageSize.values().length));
        }

        validateAllImagesExistByUid(requestImages, allImages);

        Map<String, List<ProductImageEntity>> imagesByUid = allImages.stream()
                .collect(Collectors.groupingBy(ProductImageEntity::getUid));

        Map<Integer, String> currentPositions = new HashMap<>();
        for (ProductImageEntity image : allImages) {
            currentPositions.put(image.getPosition(), image.getUid());
        }

        Set<ProductImageEntity> toSave = new HashSet<>();
        resolvePositionConflicts(requestImages, imagesByUid, currentPositions, toSave);
        assignNewPositions(requestImages, imagesByUid, currentPositions, toSave);
        List<ProductImageEntity> updatedImages = imageRepository.saveAll(toSave);

        return ProductImagesResponse.builder()
                .images(imageMapper.toProductImageDto(updatedImages))
                .build();
    }

    private void resolvePositionConflicts(List<ProductImagesChangeRequest.Image> requestImages,
                                          Map<String, List<ProductImageEntity>> imagesByUid,
                                          Map<Integer, String> currentPositions,
                                          Set<ProductImageEntity> toSave) {
        for (ProductImagesChangeRequest.Image requestImage : requestImages) {
            String uid = requestImage.uid();
            int newPosition = requestImage.position();

            List<ProductImageEntity> group = imagesByUid.get(uid);
            if (group == null) continue;

            String occupyingUid = currentPositions.get(newPosition);
            if (occupyingUid != null && !occupyingUid.equals(uid)) {
                List<ProductImageEntity> conflictGroup = imagesByUid.get(occupyingUid);
                for (ProductImageEntity conflictImage : conflictGroup) {
                    int oldPosition = conflictImage.getPosition();
                    int shiftedPosition = oldPosition + TEMP_OFFSET;

                    conflictImage.setPosition(shiftedPosition);
                    toSave.add(conflictImage);

                    currentPositions.remove(oldPosition);
                    currentPositions.put(shiftedPosition, conflictImage.getUid());
                }
            }
        }
    }

    private void assignNewPositions(List<ProductImagesChangeRequest.Image> requestImages,
                                    Map<String, List<ProductImageEntity>> imagesByUid,
                                    Map<Integer, String> currentPositions,
                                    Set<ProductImageEntity> toSave) {
        for (ProductImagesChangeRequest.Image requestImage : requestImages) {
            String uid = requestImage.uid();
            int newPosition = requestImage.position();

            List<ProductImageEntity> group = imagesByUid.get(uid);
            if (group == null) continue;

            for (ProductImageEntity image : group) {
                image.setPosition(newPosition);
                toSave.add(image);
            }

            currentPositions.put(newPosition, uid);
        }
    }

    private void validateAllImagesExistByUid(List<ProductImagesChangeRequest.Image> imagesFromRequest,
                                             List<ProductImageEntity> allImages) {
        Set<String> dbUid = allImages.stream()
                .map(ProductImageEntity::getUid)
                .collect(Collectors.toSet());

        Set<String> missingUid = imagesFromRequest.stream()
                .map(ProductImagesChangeRequest.Image::uid)
                .filter(uid -> !dbUid.contains(uid))
                .collect(Collectors.toSet());

        if (!missingUid.isEmpty()) {
            throw new ImageNotFoundException(String.format(ProductImagesResponseMessages.ERROR_IMAGE_NOT_FOUND_BY_UID, missingUid));
        }
    }


    public ProductImagesResponse getProductImages(Long productId) {

        ensureProductExists(productId);

        List<ProductImageEntity> entities = imageRepository.findByProductId(productId);

        return ProductImagesResponse.builder()
                .images(imageMapper.toProductImageDto(entities))
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
        try {
            String publicId = entity.getLink();
            storageService.deleteByUrl(publicId);
        } catch (IOException e) {
            throw new RuntimeException(entity.getLink(), e); //TODO need to implement SCRUM-211.
            // Need implement global CloudStorageException??
            // You can insert the team lead's resolution here
        }
    }
}