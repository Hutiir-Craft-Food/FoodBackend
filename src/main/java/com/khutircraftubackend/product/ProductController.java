package com.khutircraftubackend.product;

import com.khutircraftubackend.product.request.ProductRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/v1/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	private final ProductMapper productMapper;
	private final SellerService sellerService;
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponse createProduct(
			@Valid @RequestBody ProductRequest request)	{

		ProductEntity newProduct = productService.createProduct(request);

		return productMapper.toProductResponse(newProduct);
	}

	@PutMapping(value = "/{productId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.assertCanModifyProduct(#productId))")
	@ResponseStatus(HttpStatus.OK)
	public ProductResponse updateProduct(
			@PathVariable Long productId,
			@Valid @RequestBody ProductRequest request) {

		ProductEntity updatedProduct = productService.updateProduct(productId, request);

		return productMapper.toProductResponse(updatedProduct);
	}

	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.assertCanModifyProduct(#productId))")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable Long productId) {

		productService.deleteProduct(productId);
	}

	@DeleteMapping("/delete-all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllProductsForCurrentSeller() {

		SellerEntity currentSeller = sellerService.getCurrentSeller();

		productService.deleteAllProductsForSeller(currentSeller);
	}

	@DeleteMapping("delete-all/seller-id/{sellerId}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllProductsForSeller(@PathVariable Long sellerId) {
		
		SellerEntity seller = sellerService.getSellerId(sellerId);
		
		productService.deleteAllProductsForSeller(seller);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Object> getAllProducts(
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "4") int limit) {
		
		return productService.getProducts(offset, limit);
	}
	
	@GetMapping("/featured")
	@ResponseStatus(HttpStatus.OK)
	public Collection<ProductResponse> getFeaturedProducts(
			@RequestParam(defaultValue = "16") int limit) {
		
		return productService.getLatestProducts(limit);
	}
	
}
