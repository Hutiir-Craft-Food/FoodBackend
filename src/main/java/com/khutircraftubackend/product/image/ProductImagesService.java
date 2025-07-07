package com.khutircraftubackend.product.image;

import static com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges.ImagesUploadAndChanges;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.exception.httpstatus.BadRequestException;
import com.khutircraftubackend.product.image.request.ProductImagesUploadAndChanges;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class ProductImagesService {

    private final ProductImagesRepository imageRepository;
    private final ProductService productService;
    private final ProductImagesMapper imageMapper;
    //    private final CloudinaryService cloudinaryService; //TODO нет доступа до настроик клаудинари
    private static final Long MAX_COUNT_FILES = 5L;


    @Transactional
    public ProductImagesResponse uploadImages(Long productId, ProductImagesUploadAndChanges jsonImages,
                                              List<MultipartFile> fileImages) {
        ProductEntity product = productService.findProductById(productId);

        if (fileImages.size() > MAX_COUNT_FILES) {
            throw new BadRequestException
                    (String.format(ProductImagesResponseMessages.ERROR_TOO_MANY_IMAGES, MAX_COUNT_FILES));
        }

        if(hasAnyDuplicatePosition(product, jsonImages)){
            throw new BadRequestException(ProductImagesResponseMessages.ERROR_POSITION_EXIST);
        }

        List<ProductImagesEntity> savedImages = IntStream.range(0, fileImages.size())
                .mapToObj(i -> mapToEntities(product, jsonImages.images().get(i), fileImages))
                .flatMap(List::stream)
                .toList();

        List<ProductImagesEntity> entities = imageRepository.saveAll(savedImages);

        return imageMapper.toResponseDto(entities);
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

    private boolean hasAnyDuplicatePosition(ProductEntity product, ProductImagesUploadAndChanges jsonImage) {
        Set<Integer> existingPositions = imageRepository.findByProductId(product.getId()).stream()
                .map(ProductImagesEntity::getPosition)
                .collect(Collectors.toSet());

        return jsonImage.images().stream()
                .map(ImagesUploadAndChanges::position)
                .anyMatch(existingPositions::contains);
    }

    private MultipartFile findFileByUid(List<MultipartFile> files, String uid) {
        return files.stream()
                .filter(file -> Objects.equals(file.getOriginalFilename(), uid))
                .findFirst()
                .orElseThrow(() ->
                        new BadRequestException(String.format(ProductImagesResponseMessages.ERROR_NAME_NOT_FOUND, uid)));
    }

    @Transactional
    public ProductImagesResponse changesPosition(Long productId, ProductImagesUploadAndChanges request1) {

        List<ImagesUploadAndChanges> requestImages = request1.images();
        List<ProductImagesEntity> allImages = imageRepository.findByProductId(productId);

        Map<String, List<ProductImagesEntity>> imagesByUid = allImages.stream()
                .collect(Collectors.groupingBy(ProductImagesEntity::getUid));

        Map<Integer, String> currentPositions = allImages.stream()
                .collect(Collectors.toMap(
                        ProductImagesEntity::getPosition,
                        ProductImagesEntity::getUid
                ));

        Set<ProductImagesEntity> toSave = new HashSet<>();
        int tempOffset = 10;

        for (ImagesUploadAndChanges requestImage : requestImages) {

            String uid = requestImage.uid();
            int newPosition = requestImage.position();
            List<ProductImagesEntity> group = imagesByUid.get(uid);

            if (group == null) continue;

            String conflictingUid = currentPositions.get(newPosition);
            if (conflictingUid != null && !conflictingUid.equals(uid)) {
                List<ProductImagesEntity> conflictingGroup = imagesByUid.get(conflictingUid);
                for (ProductImagesEntity conflictImage : conflictingGroup) {
                    conflictImage.setPosition(conflictImage.getPosition() + tempOffset);
                    toSave.add(conflictImage);
                }

                currentPositions.put(conflictingGroup.get(0).getPosition(), conflictingUid);
            }
        }

        for (ImagesUploadAndChanges requestImage : requestImages) {
            String uid = requestImage.uid();
            int newPosition = requestImage.position();

            List<ProductImagesEntity> group = imagesByUid.get(uid);
            if (group == null) continue;

            for (ProductImagesEntity image : group) {
                image.setPosition(newPosition);
                toSave.add(image);
            }
        }

        imageRepository.saveAll(toSave);

        return imageMapper.toResponseDto(allImages);
    }

    private String getLink(MultipartFile file, ImageSizes size) {
        return switch (size) { //TODO заглушки
            case LARGE -> String.format("URL - %s", size);
            case SMALL -> String.format("URL - %s", size);
            case MEDIUM -> String.format("URL - %s", size);
            case THUMBNAIL -> String.format("URL - %s", size);
        };
    }

    public ProductImagesResponse getProductImages(Long productId) {
        List<ProductImagesEntity> entities = imageRepository.findByProductId(productId);
        return imageMapper.toResponseDto(entities);
    }

    public void deleteProductImages(Long productId, List<Integer> positionIds) {
        List<ProductImagesEntity> entities = imageRepository.findByProductId(productId);

        List<ProductImagesEntity> toDelete = (positionIds == null || positionIds.isEmpty())
                ? entities
                : entities.stream()
                .filter(e -> positionIds.contains(e.getPosition()))
                .toList();

        toDelete.forEach(this::safeDeleteFromCloud);

        imageRepository.deleteAll(toDelete);
    }

    private void safeDeleteFromCloud(ProductImagesEntity entity) {
//        try {
        String publicId = entity.getLink();
        //cloudinaryService.deleteByUrl(publicId);
//        } catch (IOException e) {
//            throw new RuntimeException(entity.getLink(), e);
//        }
    }


}
