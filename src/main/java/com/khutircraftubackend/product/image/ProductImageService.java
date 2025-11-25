package com.khutircraftubackend.product.image;

import com.khutircraftubackend.exception.FileReadingException;
import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
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
    private final ProductImageValidator validator;

    private static final int TEMP_POSITION_OFFSET = 100;

    @Value("${allowed.mime.types}")
    private Set<String> allowedMimeTypes;

    @Transactional()
    public ProductImageResponse uploadImages(Long productId, ProductImageUploadRequest request,
                                             List<MultipartFile> files) {
        ProductEntity product = ensureProductExists(productId);
        List<ProductImageEntity> existingImages = imageRepository.findByProductId(productId);
        validator.validateUploadRequest(existingImages, request, files);
        mimeValidator.validateMimeTypes(files, allowedMimeTypes);

        List<ProductImageEntity> createdImages = createImagesInternal(product, request, files);
        List<ProductImageEntity> saved = imageRepository.saveAll(createdImages);

        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(saved))
                .build();
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

    @Transactional()
    public ProductImageResponse reorderImages(Long productId, ProductImageChangeRequest request) {

        ensureProductExists(productId);
        List<ProductImageEntity> allImages = imageRepository.findByProductId(productId);
        validator.validateImageIds(request, allImages);

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
        validator.validateDeletePositions(positions, all);
        List<ProductImageEntity> target = (positions == null || positions.isEmpty())
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
}