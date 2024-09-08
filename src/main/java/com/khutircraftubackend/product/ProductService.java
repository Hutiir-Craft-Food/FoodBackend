package com.khutircraftubackend.product;

import com.khutircraftubackend.exception.product.ProductNotFoundException;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.image.FileUploadService;
import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerService sellerService;
    private final FileUploadService fileUploadService;
    private final FileConverterService fileConverterService;

    private ProductEntity findProductById(Long productId) {
        return productRepository.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
    }

    public boolean canModifyProduct(Long productId) {
        ProductEntity existingProduct = findProductById(productId);
        SellerEntity currentSeller = sellerService.getCurrentSeller();
        return (existingProduct.getSeller().getCompany().equals(currentSeller.getCompany()));
    }

    private void validateSellerCompany(SellerEntity currentSeller, SellerEntity requestSeller) throws AccessDeniedException {
        if (!currentSeller.getCompany().equals(requestSeller.getCompany())) {
            throw new AccessDeniedException("You do not have permission to create for this company.");
        }
    }


    @Transactional
    public ProductEntity createProduct(String name, MultipartFile thumbnailImage, MultipartFile image, Boolean available, String description, Long sellerId) throws IOException {

        SellerEntity seller = sellerService.getSellerId(sellerId);

        ProductCreateRequest request = new ProductCreateRequest(name, thumbnailImage, image, available, description, seller);

        SellerEntity currentSeller = sellerService.getCurrentSeller();
        validateSellerCompany(currentSeller, request.seller());
        ProductMapper.INSTANCE.validateCreateRequest(request);
        ProductEntity productEntity = ProductMapper.INSTANCE.toProductEntity(request);
        productEntity.setImageUrl(fileConverterService.convert(request.image()));
        productEntity.setThumbnailImageUrl(fileConverterService.convert(request.thumbnailImage()));
        productEntity.setSeller(currentSeller);

        return productRepository.save(productEntity);
    }


    @Transactional
    public ProductEntity patchProduct(Long productId, ProductUpdateRequest request,
                                      MultipartFile thumbnailImageFile, MultipartFile imageFile) throws IOException {
        ProductEntity existingProduct = findProductById(productId);
        if (request.name() != null) {
            existingProduct.setName(request.name());
        }
        if (thumbnailImageFile != null && !thumbnailImageFile.isEmpty()) {
            String thumbnailImageUrl = fileConverterService.convert(thumbnailImageFile);
            existingProduct.setThumbnailImageUrl(thumbnailImageUrl);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileConverterService.convert(imageFile);
            existingProduct.setImageUrl(imageUrl);
        }
        if (request.available() != null) {
            existingProduct.setAvailable(request.available());
        }
        if (request.description() != null) {
            existingProduct.setDescription(request.description());
        }
        return productRepository.save(existingProduct);
    }

    @Transactional
    public ProductEntity updateProduct(Long productId, ProductUpdateRequest request,
                                       MultipartFile thumbnailImageFile, MultipartFile imageFile) throws IOException {
        ProductEntity existingProduct = findProductById(productId);
        ProductMapper.INSTANCE.validateUpdateRequest(request);
        ProductMapper.INSTANCE.updateProductFromRequest(existingProduct, request);
        if (thumbnailImageFile != null && !thumbnailImageFile.isEmpty()) {
            String thumbnailImageUrl = fileConverterService.convert(thumbnailImageFile);
            existingProduct.setThumbnailImageUrl(thumbnailImageUrl);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileConverterService.convert(imageFile);
            existingProduct.setImageUrl(imageUrl);
        }
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long productId) throws IOException {
        ProductEntity existingProduct = findProductById(productId);
        if (existingProduct.getThumbnailImageUrl() != null) {
            String publicId = extractPublicId(existingProduct.getThumbnailImageUrl());
            fileUploadService.deleteCloudinaryById(publicId);
        }
        if (existingProduct.getImageUrl() != null) {
            String publicId = extractPublicId(existingProduct.getImageUrl());
            fileUploadService.deleteCloudinaryById(publicId);
        }
        productRepository.delete(existingProduct);
    }

    @Transactional
    public void deleteAllProductsForCurrentSeller() throws IOException {
        SellerEntity currentSeller = sellerService.getCurrentSeller();
        List<ProductEntity> products = productRepository.findAllBySeller(currentSeller);

        for (ProductEntity product : products) {
            if (product.getThumbnailImageUrl() != null) {
                String publicId = extractPublicId(product.getThumbnailImageUrl());
                fileUploadService.deleteCloudinaryById(publicId);
            }
            if (product.getImageUrl() != null) {
                String publicId = extractPublicId(product.getImageUrl());
                fileUploadService.deleteCloudinaryById(publicId);
            }
        }
        productRepository.deleteBySeller(currentSeller);
    }

    private String extractPublicId(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }

    public List<ProductEntity> getProducts(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return productRepository.findAllBy(pageable);
    }
}
