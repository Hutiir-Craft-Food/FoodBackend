package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.exception.httpstatus.NotFoundException;
import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.Map;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.NO_ACCESS;
import static com.khutircraftubackend.product.exception.ProductResponseMessage.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {
	
	private final ProductRepository productRepository;
	private final SellerService sellerService;
	private final CategoryService categoryService;
	private final ProductMapper productMapper;

	public ProductEntity findProductById(Long productId) {

		return productRepository.findProductById(productId)
				.orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
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
}
