package com.khutircraftubackend.product.image;

import static com.khutircraftubackend.product.image.request.ProductImageUploadRequest.Image;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.exception.httpstatus.BadRequestException;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.validated.ImageMimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class ProductImagesService {

    private final ProductImagesRepository imageRepository;
    private final ProductService productService;
    private final ProductImageMapper imageMapper;
    private final StorageService storageService;
    private final ImageMimeValidator mimeValidator;
    private static final Long MAX_COUNT_FILES = 5L;
    private static final int TEMP_OFFSET = 100;
    private static final String ALLOWED_MIME_TYPE = "image/"; //TODO what images do we allow?? Because of this,
                                                              // maybe we need to change the checking logic


    @Transactional
    public ProductImageResponse uploadImages(Long productId, ProductImageUploadRequest json,
                                             List<MultipartFile> files) {
        ProductEntity product = getValidProductOrThrow(productId);
        mimeValidator.validateMimeTypes(files, ALLOWED_MIME_TYPE);

        if (files.size() > MAX_COUNT_FILES) {
            throw new BadRequestException
                    (String.format(ProductImageResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_COUNT_FILES));
        }

        validateNoDuplicates(productId, json);

        List<ProductImageEntity> savedImages = IntStream.range(0, files.size())
                .mapToObj(i -> mapToEntities(product, json.images().get(i), files))
                .flatMap(List::stream)
                .toList();

        List<ProductImageEntity> entities = imageRepository.saveAll(savedImages);

        return imageMapper.toResponseDto(entities);
    }

    private void validateNoDuplicates(Long productId, ProductImageUploadRequest jsonImage) {

        if (hasAnyDuplicatePosition(productId, jsonImage)) {
            throw new BadRequestException(ProductImageResponseMessages.ERROR_POSITION_EXIST);
        }

        if (hasExistingUid(productId, jsonImage)) {
            throw new BadRequestException(ProductImageResponseMessages.ERROR_UID_EXIST);
        }
    }

    private boolean hasAnyDuplicatePosition(Long productId, ProductImageUploadRequest jsonImage) {
        Set<Integer> existingPositions = imageRepository.findByProductId(productId).stream()
                .map(ProductImageEntity::getPosition)
                .collect(Collectors.toSet());

        return jsonImage.images().stream()
                .map(Image::position)
                .anyMatch(existingPositions::contains);
    }

    private boolean hasExistingUid(Long productId, ProductImageUploadRequest jsonImage) {
        Set<String> existingUid = imageRepository.findByProductId(productId).stream()
                .map(ProductImageEntity::getUid)
                .collect(Collectors.toSet());

        return jsonImage.images().stream()
                .map(Image::uid)
                .anyMatch(existingUid::contains);
    }

    private List<ProductImageEntity> mapToEntities(ProductEntity product, Image jsonImage,
                                                   List<MultipartFile> fileImage) {
        List<ImageSizes> sizes = List.of(ImageSizes.THUMBNAIL, ImageSizes.SMALL, ImageSizes.MEDIUM, ImageSizes.LARGE);

        return sizes.stream()
                .map(size -> ProductImageEntity.builder()
                        .uid(jsonImage.uid())
                        .product(product)
                        .link(getLink(findFileByUid(fileImage, jsonImage.uid()), size))
                        .tsSize(size)
                        .position(jsonImage.position())
                        .build())
                .toList();
    }

    private MultipartFile findFileByUid(List<MultipartFile> files, String uid) {
        return files.stream()
                .filter(file -> Objects.equals(file.getOriginalFilename(), uid))
                .findFirst()
                .orElseThrow(() ->
                        new BadRequestException(String.format(ProductImageResponseMessages.ERROR_NAME_NOT_FOUND, uid)));
    }

    @Transactional
    public ProductImageResponse changesPosition(Long productId, ProductImageUploadRequest request) {
        getValidProductOrThrow(productId);
        List<Image> requestImages = request.images();
        List<ProductImageEntity> allImages = imageRepository.findByProductId(productId);

        if (allImages.size() != requestImages.size() * ImageSizes.values().length){
            throw new BadRequestException(ProductImageResponseMessages.ERROR_SIZE);
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

        List<ProductImageEntity> updatedImages  = imageRepository.saveAll(toSave);

        return imageMapper.toResponseDto(updatedImages);
    }

    private void resolvePositionConflicts(List<Image> requestImages,
                                          Map<String, List<ProductImageEntity>> imagesByUid,
                                          Map<Integer, String> currentPositions,
                                          Set<ProductImageEntity> toSave) {
        for (Image requestImage : requestImages) {
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

    private void assignNewPositions(List<Image> requestImages,
                                    Map<String, List<ProductImageEntity>> imagesByUid,
                                    Map<Integer, String> currentPositions,
                                    Set<ProductImageEntity> toSave) {
        for (Image requestImage : requestImages) {
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

    private void validateAllImagesExistByUid
            (List<Image> requestImages, List<ProductImageEntity> allImages) {
        Set<String> dbUid = allImages.stream()
                .map(ProductImageEntity::getUid)
                .collect(Collectors.toSet());

        Set<String> missingUid = requestImages.stream()
                .map(Image::uid)
                .filter(uid -> !dbUid.contains(uid))
                .collect(Collectors.toSet());

        if (!missingUid.isEmpty()) {
            throw new BadRequestException(String.format(ProductImageResponseMessages.ERROR_MISSING_UID, missingUid));
        }
    }

    private String getLink(MultipartFile file, ImageSizes size) {
        return switch (size) { //TODO need to implement SCRUM-XXX. Stubs for resize
            case LARGE -> String.format("URL - %s", size);
            case SMALL -> String.format("URL - %s", size);
            case MEDIUM -> String.format("URL - %s", size);
            case THUMBNAIL -> String.format("URL - %s", size);
        };
    }

    public ProductImageResponse getProductImages(Long productId) {
        getValidProductOrThrow(productId);
        List<ProductImageEntity> entities = imageRepository.findByProductId(productId);
        return imageMapper.toResponseDto(entities);
    }

    @Transactional
    public void deleteProductImages(Long productId, List<Integer> positionIds) {
        getValidProductOrThrow(productId);
        List<ProductImageEntity> entities = imageRepository.findByProductId(productId);

        List<ProductImageEntity> toDelete = (positionIds == null || positionIds.isEmpty())
                ? entities
                : entities.stream()
                .filter(e -> positionIds.contains(e.getPosition()))
                .toList();

        toDelete.forEach(this::safeDeleteFromCloud);

        imageRepository.deleteAll(toDelete);
    }

    private ProductEntity getValidProductOrThrow(Long productId) {
        return productService.findProductById(productId);
    }

    private void safeDeleteFromCloud(ProductImageEntity entity) {
        try {
            String publicId = entity.getLink();
            storageService.deleteByUrl(publicId);
        } catch (IOException e) {
            throw new RuntimeException(entity.getLink(), e); //TODO need to implement SCRUM-XXX.
                                                             // Need implement global CloudStorageException??
                                                             // You can insert the team lead's resolution here
                                                             //
        }
    }
}
