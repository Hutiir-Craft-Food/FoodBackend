package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.search.KeywordService;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import com.khutircraftubackend.storage.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private String handleIcon(MultipartFile iconFile) throws IOException, URISyntaxException {
		
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
	public ProductEntity createProduct(ProductRequest request, MultipartFile thumbnailImage, MultipartFile image) throws IOException, URISyntaxException {
		
		SellerEntity currentSeller = sellerService.getCurrentSeller();
		
		ProductEntity productEntity = productMapper.toProductEntity(request);
		
		CategoryEntity category = categoryService.findCategoryById(request.categoryId());
		
		productEntity.setImageUrl(handleIcon(image));
		productEntity.setThumbnailImageUrl(handleIcon(thumbnailImage));
		
		productEntity.setCategory(category);
		productEntity.setSeller(currentSeller);
		
		Set<String> keywords = KeywordService.generateKeywords(productEntity, category);
		productEntity.setKeywords(keywords);
		
		return productRepository.save(productEntity);
	}
	
	@Transactional
	public ProductEntity updateProduct(Long productId, ProductRequest request,
									   MultipartFile thumbnailImageFile, MultipartFile imageFile) throws IOException, URISyntaxException {
		
		ProductEntity existingProduct = findProductById(productId);
		
		productMapper.updateProductFromRequest(existingProduct, request);
		
		CategoryEntity categoryToUse = existingProduct.getCategory();
		
		if (request.categoryId() != null) {
			categoryToUse = categoryService.findCategoryById(request.categoryId());
			existingProduct.setCategory(categoryToUse);
		}
		
		existingProduct.setThumbnailImageUrl(handleIcon(thumbnailImageFile));
		existingProduct.setImageUrl(handleIcon(imageFile));
		
		Set<String> keywords = KeywordService.generateKeywords(existingProduct, categoryToUse);
		existingProduct.setKeywords(keywords);
		
		return productRepository.save(existingProduct);
	}
	
	@Transactional
	public void deleteProduct(Long productId) throws IOException, URISyntaxException {
		
		ProductEntity existingProduct = findProductById(productId);
		
		deleteProductImages(existingProduct);
		
		productRepository.delete(existingProduct);
	}
	
	@Transactional
	public void deleteAllProductsForSeller(SellerEntity seller) throws IOException, URISyntaxException {
		
		List<ProductEntity> products = productRepository.findAllBySeller(seller);
		
		if (products != null) {
			for (ProductEntity product : products) {
				deleteProductImages(product);
			}
		}
		
		productRepository.deleteBySeller(seller);
	}
	
	private void deleteProductImages(ProductEntity product) throws IOException, URISyntaxException {
		
		if (product.getThumbnailImageUrl() != null) {
			storageService.deleteByUrl(product.getThumbnailImageUrl());
		}
		
		if (product.getImageUrl() != null) {
			storageService.deleteByUrl(product.getImageUrl());
		}
	}
	
	public Map<String, Object> getProducts(int offset, int limit) {
		
		Pageable pageable = PageRequest.of(offset, limit);
		Collection<ProductEntity> productEntities = productRepository.findAllBy(pageable);
		
		Collection<ProductResponse> products = productMapper.toProductResponse(productEntities);
		
		long total = productRepository.count();
		
		return Map.of(
				"products", products,
				"total", total,
				"offset", offset,
				"limit", limit
		);
	}
}
