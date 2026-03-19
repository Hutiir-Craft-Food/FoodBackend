package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductService;
import com.khutircraftubackend.product.image.exception.DuplicateImagePositionException;
import com.khutircraftubackend.product.image.exception.ImageProcessingException;
import com.khutircraftubackend.product.image.request.ProductImageChangeRequest;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import com.khutircraftubackend.product.image.response.ProductImageResponse;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.validated.ImageMimeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.khutircraftubackend.product.image.response.ProductImageResponseMessages.ERROR_IMAGE_READ_STREAM;
import static com.khutircraftubackend.product.image.response.ProductImageResponseMessages.ERROR_UPLOAD_IMAGE_VARIANTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {
    
    private final ProductImageRepository imageRepository;
    private final ProductService productService;
    private final ProductImageMapper imageMapper;
    private final StorageService storageService;
    private final ImageMimeValidator mimeValidator;
    private final ProductImageValidator validator;
    private final ImageProcessing imageProcessing;
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
        List<ProductImageEntity> saved;
        try {
            saved = imageRepository.saveAll(createdImages);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateImagePositionException(ProductImageResponseMessages.ERROR_UNIQUE_POSITION);
        }
        
        existingImages.addAll(saved);
        
        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(existingImages))
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
        ProductImageEntity productImageEntity = ProductImageEntity.builder()
                .product(product)
                .position(position)
                .build();
        
        List<String> uploadedUrls = new ArrayList<>();
        
        Map<ImageSize, byte[]> processed;
        
        try (InputStream inputStream = file.getInputStream()) {
            processed = imageProcessing.process(inputStream);
        } catch (IOException e) {
            log.error("Failed to process image at position {}", position, e);
            throw new ImageProcessingException(ERROR_IMAGE_READ_STREAM, e);
        }
        
        List<ProductImageVariantEntity> variants = new ArrayList<>(processed.size());
        try {
            for (Map.Entry<ImageSize, byte[]> entry : processed.entrySet()) {
                
                ImageSize size = entry.getKey();
                byte[] imageBytes = entry.getValue();
                
                String fileName = size.name().toLowerCase() + "_" + UUID.randomUUID() + ".jpg";
                
                String url = storageService.upload(imageBytes, fileName);
                
                uploadedUrls.add(url);
                
                ProductImageVariantEntity variant = ProductImageVariantEntity.builder()
                        .image(productImageEntity)
                        .tsSize(size)
                        .link(url)
                        .build();
                
                variants.add(variant);
            }
            productImageEntity.setVariants(variants);
            
            return productImageEntity;
            
        } catch (RuntimeException e) {
            log.error("Failed to upload image variants for position {}. Starting rollback...", position, e);
            rollback(uploadedUrls);
            throw new ImageProcessingException(ERROR_UPLOAD_IMAGE_VARIANTS, e);
        }
    }
    
    private void rollback(List<String> urls) {
        
        for(String url : urls) {
            try {
                storageService.deleteByUrl(url);
            } catch (RuntimeException ex) {
                log.error("Failed to rollback file: {}", url, ex);
            }
        }
    }
    
    
    @Transactional
    public ProductImageResponse reorderImages(Long productId, ProductImageChangeRequest request) {
        ensureProductExists(productId);
        List<ProductImageEntity> allImages = imageRepository.findByProductId(productId);
        validator.validateImageIds(request, allImages);
        imageRepository.shiftPositions(productId);
        applyNewPositionsAfterShift(productId, request);
        List<ProductImageEntity> updated = imageRepository.findByProductId(productId);
        
        return ProductImageResponse.builder()
                .images(imageMapper.toProductImageDtoList(updated))
                .build();
    }
    
    private void applyNewPositionsAfterShift(Long productId, ProductImageChangeRequest request) {
        request.images().forEach(img ->
                imageRepository.updatePosition(productId, img.id(), img.position())
        );
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