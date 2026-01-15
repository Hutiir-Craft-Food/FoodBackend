package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.exception.InvalidPaginationParameterException;
import com.khutircraftubackend.product.exception.ProductAccessException;
import com.khutircraftubackend.product.exception.ProductNotFoundException;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.*;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private static final int FEATURED_MAX_LIMIT = 16;

	private final ProductRepository productRepository;
	private final SellerService sellerService;
	private final CategoryService categoryService;
	private final ProductMapper productMapper;

	public ProductEntity findProductById(Long productId) {

		return productRepository.findProductById(productId)
				.orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND, productId)));
	}

	public boolean assertCanModifyProduct(Long productId) {

		ProductEntity existingProduct = findProductById(productId);
		SellerEntity currentSeller = sellerService.getCurrentSeller();

		if (!currentSeller.equals(existingProduct.getSeller())) {
			throw new ProductAccessException(PRODUCT_ACCESS_DENIED);
		}

		return true;
	}

	@Transactional
	public ProductEntity createProduct(ProductRequest request) {
		SellerEntity currentSeller = sellerService.getCurrentSeller();
		ProductEntity productEntity = productMapper.toProductEntity(request);
		CategoryEntity categoryEntity = categoryService.findCategoryById(request.categoryId());
		productEntity.setCategory(categoryEntity);
		productEntity.setSeller(currentSeller);
		return productRepository.save(productEntity);
	}


	@Transactional
	public ProductEntity updateProduct(Long productId, ProductRequest request) {
		ProductEntity existingProduct = findProductById(productId);
		productMapper.updateProductFromRequest(existingProduct, request);
		CategoryEntity categoryToUse;

		if (request.categoryId() != null) {
			categoryToUse = categoryService.findCategoryById(request.categoryId());
			existingProduct.setCategory(categoryToUse);
		}

		return productRepository.save(existingProduct);
	}

	@Transactional
	public void deleteProduct(Long productId) {
		ProductEntity existingProduct = findProductById(productId);
		productRepository.delete(existingProduct);
	}

	@Transactional
	public void deleteAllProductsForSeller(SellerEntity seller) {
		productRepository.deleteBySeller(seller);
	}


	@Transactional(readOnly = true)
	public Map<String, Object> getProducts(int offset, int limit) {

		Pageable pageable = PageRequest.of(offset, limit);
		Page<ProductEntity> productEntities = productRepository.findAllBy(pageable);

		Collection<ProductResponse> products = productMapper.toProductResponse(productEntities);

		long total = productRepository.count();
		
		return Map.of(
				"products", products,
				"total", total,
				"offset", offset,
				"limit", limit
		);
	}

	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long productId){
		ProductEntity product = findProductById(productId);
		return productMapper.toProductResponse(product);
	}

	@Transactional(readOnly = true)
	public Collection<ProductResponse> getLatestProducts(int limit) {

		if (limit <= 0) {
			throw new InvalidPaginationParameterException(LIMIT_EXCEEDED);
		}

		int size = Math.min(limit, FEATURED_MAX_LIMIT);

		Pageable pageable = PageRequest.of(0, size,
				Sort.by("createdAt").descending());

		Page<ProductEntity> productPage = productRepository.findAll(pageable);

		return productMapper.toProductResponse(productPage.getContent());
	}

}