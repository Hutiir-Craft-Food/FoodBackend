package com.khutircraftubackend.product.image;

import static com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges.ImagesUploadAndChanges;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.exception.httpstatus.BadRequestException;
import com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges;
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
    private final ProductImagesMapper imageMapper;
    private final StorageService storageService;
    private final ImageMimeValidator mimeValidator;
    private static final Long MAX_COUNT_FILES = 5L;
    private static final int TEMP_OFFSET = 100;
    private static final String ALLOWED_MIME_TYPE = "image/"; //TODO what images do we allow?? Because of this,
                                                              // maybe we need to change the checking logic


    @Transactional
    public ProductImagesResponse uploadImages(Long productId, ProductImagesUploadAndChanges json,
                                              List<MultipartFile> files) {
        ProductEntity product = getValidProductOrThrow(productId);
        mimeValidator.validateMimeTypes(files, ALLOWED_MIME_TYPE);

        if (files.size() > MAX_COUNT_FILES) {
            throw new BadRequestException
                    (String.format(ProductImagesResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_COUNT_FILES));
        }

        validateNoDuplicates(productId, json);

        List<ProductImagesEntity> savedImages = IntStream.range(0, files.size())
                .mapToObj(i -> mapToEntities(product, json.images().get(i), files))
                .flatMap(List::stream)
                .toList();

        List<ProductImagesEntity> entities = imageRepository.saveAll(savedImages);

        return imageMapper.toResponseDto(entities);
    }

    private void validateNoDuplicates(Long productId, ProductImagesUploadAndChanges jsonImage) {

        if (hasAnyDuplicatePosition(productId, jsonImage)) {
            throw new BadRequestException(ProductImagesResponseMessages.ERROR_POSITION_EXIST);
        }

        if (hasExistingUid(productId, jsonImage)) {
            throw new BadRequestException(ProductImagesResponseMessages.ERROR_UID_EXIST);
        }
    }

    private boolean hasAnyDuplicatePosition(Long productId, ProductImagesUploadAndChanges jsonImage) {
        Set<Integer> existingPositions = imageRepository.findByProductId(productId).stream()
                .map(ProductImagesEntity::getPosition)
                .collect(Collectors.toSet());

        return jsonImage.images().stream()
                .map(ImagesUploadAndChanges::position)
                .anyMatch(existingPositions::contains);
    }

    private boolean hasExistingUid(Long productId, ProductImagesUploadAndChanges jsonImage) {
        Set<String> existingUid = imageRepository.findByProductId(productId).stream()
                .map(ProductImagesEntity::getUid)
                .collect(Collectors.toSet());

        return jsonImage.images().stream()
                .map(ImagesUploadAndChanges::uid)
                .anyMatch(existingUid::contains);
    }

    private List<ProductImagesEntity> mapToEntities(ProductEntity product, ImagesUploadAndChanges jsonImage,
                                                    List<MultipartFile> fileImage) {
        List<ImageSizes> sizes = List.of(ImageSizes.THUMBNAIL, ImageSizes.SMALL, ImageSizes.MEDIUM, ImageSizes.LARGE);

        return sizes.stream()
                .map(size -> ProductImagesEntity.builder()
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
                        new BadRequestException(String.format(ProductImagesResponseMessages.ERROR_NAME_NOT_FOUND, uid)));
    }

    @Transactional
    public ProductImagesResponse changesPosition(Long productId, ProductImagesUploadAndChanges request) {
        getValidProductOrThrow(productId);
        List<ImagesUploadAndChanges> requestImages = request.images();
        List<ProductImagesEntity> allImages = imageRepository.findByProductId(productId);

        if (allImages.size() != requestImages.size() * ImageSizes.values().length){
            throw new BadRequestException(ProductImagesResponseMessages.ERROR_SIZE);
        }

        validateAllImagesExistByUid(requestImages, allImages);

        Map<String, List<ProductImagesEntity>> imagesByUid = allImages.stream()
                .collect(Collectors.groupingBy(ProductImagesEntity::getUid));

        Map<Integer, String> currentPositions = new HashMap<>();
        for (ProductImagesEntity image : allImages) {
            currentPositions.put(image.getPosition(), image.getUid());
        }

        Set<ProductImagesEntity> toSave = new HashSet<>();

        resolvePositionConflicts(requestImages, imagesByUid, currentPositions, toSave);
        assignNewPositions(requestImages, imagesByUid, currentPositions, toSave);

        List<ProductImagesEntity> updatedImages  = imageRepository.saveAll(toSave);

        return imageMapper.toResponseDto(updatedImages);
    }

    private void resolvePositionConflicts(List<ImagesUploadAndChanges> requestImages,
                                          Map<String, List<ProductImagesEntity>> imagesByUid,
                                          Map<Integer, String> currentPositions,
                                          Set<ProductImagesEntity> toSave) {
        for (ImagesUploadAndChanges requestImage : requestImages) {
            String uid = requestImage.uid();
            int newPosition = requestImage.position();

            List<ProductImagesEntity> group = imagesByUid.get(uid);
            if (group == null) continue;

            String occupyingUid = currentPositions.get(newPosition);
            if (occupyingUid != null && !occupyingUid.equals(uid)) {
                List<ProductImagesEntity> conflictGroup = imagesByUid.get(occupyingUid);
                for (ProductImagesEntity conflictImage : conflictGroup) {
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

    private void assignNewPositions(List<ImagesUploadAndChanges> requestImages,
                                    Map<String, List<ProductImagesEntity>> imagesByUid,
                                    Map<Integer, String> currentPositions,
                                    Set<ProductImagesEntity> toSave) {
        for (ImagesUploadAndChanges requestImage : requestImages) {
            String uid = requestImage.uid();
            int newPosition = requestImage.position();

            List<ProductImagesEntity> group = imagesByUid.get(uid);
            if (group == null) continue;

            for (ProductImagesEntity image : group) {
                image.setPosition(newPosition);
                toSave.add(image);
            }

            currentPositions.put(newPosition, uid);
        }
    }

    private void validateAllImagesExistByUid
            (List<ImagesUploadAndChanges> requestImages, List<ProductImagesEntity> allImages) {
        Set<String> dbUid = allImages.stream()
                .map(ProductImagesEntity::getUid)
                .collect(Collectors.toSet());

        Set<String> missingUid = requestImages.stream()
                .map(ImagesUploadAndChanges::uid)
                .filter(uid -> !dbUid.contains(uid))
                .collect(Collectors.toSet());

        if (!missingUid.isEmpty()) {
            throw new BadRequestException(String.format(ProductImagesResponseMessages.ERROR_MISSING_UID, missingUid));
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

    public ProductImagesResponse getProductImages(Long productId) {
        getValidProductOrThrow(productId);
        List<ProductImagesEntity> entities = imageRepository.findByProductId(productId);
        return imageMapper.toResponseDto(entities);
    }

    @Transactional
    public void deleteProductImages(Long productId, List<Integer> positionIds) {
        getValidProductOrThrow(productId);
        List<ProductImagesEntity> entities = imageRepository.findByProductId(productId);

        List<ProductImagesEntity> toDelete = (positionIds == null || positionIds.isEmpty())
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

    private void safeDeleteFromCloud(ProductImagesEntity entity) {
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
