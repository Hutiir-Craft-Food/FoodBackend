package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import com.khutircraftubackend.storage.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepository productRepository;
	private final SellerService sellerService;
	private final StorageService storageService;
	private final CategoryService categoryService;
	private final ProductMapper productMapper;
	
	private ProductEntity findProductById(Long productId) {
		
		return productRepository.findProductById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
	}
	
    private String uploadIcon(MultipartFile iconFile) throws IOException {
		
		if (iconFile == null) {
			return "";
		}
		return storageService.upload(iconFile);
	}
	
	public boolean canModifyProduct(Long productId) throws AccessDeniedException {
		
		ProductEntity existingProduct = findProductById(productId);
		SellerEntity currentSeller = sellerService.getCurrentSeller();
		
		if (!currentSeller.equals(existingProduct.getSeller())) {
			throw new AccessDeniedException("You do not have permission to create for this company.");
		}
		
		return true;
	}
	
	@Transactional
	public ProductEntity createProduct(ProductRequest request, MultipartFile thumbnailImage, MultipartFile image) throws IOException {
		
		SellerEntity currentSeller = sellerService.getCurrentSeller();
		
		ProductEntity productEntity = productMapper.toProductEntity(request);
		
		CategoryEntity categoryEntity = categoryService.findCategoryById(request.categoryId());
		
		productEntity.setImageUrl(uploadIcon(image));
		productEntity.setThumbnailImageUrl(uploadIcon(thumbnailImage));
		
		productEntity.setCategory(categoryEntity);
		productEntity.setSeller(currentSeller);
		productEntity.setCreatedAt(LocalDateTime.now());
		
		return productRepository.save(productEntity);
	}
	
	@Transactional
	public ProductEntity updateProduct(Long productId, ProductRequest request,
									   MultipartFile thumbnailImageFile, MultipartFile imageFile) throws IOException {
		
		ProductEntity existingProduct = findProductById(productId);
		
		productMapper.updateProductFromRequest(existingProduct, request);
		
		CategoryEntity categoryToUse;
		
		if (request.categoryId() != null) {
			categoryToUse = categoryService.findCategoryById(request.categoryId());
			existingProduct.setCategory(categoryToUse);
		}
		
		existingProduct.setThumbnailImageUrl(uploadIcon(thumbnailImageFile));
		existingProduct.setImageUrl(uploadIcon(imageFile));
		existingProduct.setUpdatedAt(LocalDateTime.now());
		
		return productRepository.save(existingProduct);
	}
	
	@Transactional
	public void deleteProduct(Long productId) throws IOException {
		
		ProductEntity existingProduct = findProductById(productId);
		
		deleteProductImages(existingProduct);
		
		productRepository.delete(existingProduct);
	}
	
	@Transactional
	public void deleteAllProductsForSeller(SellerEntity seller) throws IOException {
		
		List<ProductEntity> products = productRepository.findAllBySeller(seller);
		
		if (products != null) {
			for (ProductEntity product : products) {
				deleteProductImages(product);
			}
		}
		
		productRepository.deleteBySeller(seller);
	}
	
	private void deleteProductImages(ProductEntity product) throws IOException {
		
		if (product.getThumbnailImageUrl() != null) {
			storageService.deleteByUrl(product.getThumbnailImageUrl());
		}
		
		if (product.getImageUrl() != null) {
			storageService.deleteByUrl(product.getImageUrl());
		}
	}
	
	public Map<String, Object> getProducts(int offset, int limit) {

		// TODO: there is a pootential bug here
		int pageNumber = offset / limit;

		Pageable pageable = PageRequest.of(pageNumber, limit);
		Page<ProductEntity> page = productRepository.findAllBy(pageable);
		
		Collection<ProductResponse> products = productMapper.toProductResponse(page.getContent());
		
		long total = page.getTotalElements();
		
		return Map.of(
				"products", products,
				"total", total,
				"offset", offset,
				"limit", limit
		);
	}
}
