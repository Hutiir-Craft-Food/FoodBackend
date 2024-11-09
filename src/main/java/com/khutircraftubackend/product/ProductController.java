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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/v1/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	private final ProductMapper productMapper;
	private final SellerService sellerService;
	
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponse createProduct(
			@Valid @ModelAttribute ProductRequest request,
			@RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
			@RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
		
		ProductEntity newProduct = productService.createProduct(request, thumbnailImage, image);
		
		return productMapper.toProductResponse(newProduct);
	}
	
	@PutMapping(value = "/{productId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
	@ResponseStatus(HttpStatus.OK)
	public ProductResponse updateProduct(
			@PathVariable Long productId,
			@Valid @ModelAttribute ProductRequest request,
			@RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
			@RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
		
		ProductEntity updatedProduct = productService.updateProduct(productId, request, thumbnailImage, image);
		
		return productMapper.toProductResponse(updatedProduct);
	}
	
	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.canModifyProduct(#productId))")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable Long productId) throws IOException {
		
		productService.deleteProduct(productId);
	}
	
	@DeleteMapping("/delete-all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllProductsForCurrentSeller() throws IOException {
		
		SellerEntity currentSeller = sellerService.getCurrentSeller();
		
		productService.deleteAllProductsForSeller(currentSeller);
	}
	
	@DeleteMapping("delete-all/{sellerId}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllProductsForSeller(@PathVariable Long sellerId) throws IOException {
		
		SellerEntity seller = sellerService.getSellerId(sellerId);
		
		productService.deleteAllProductsForSeller(seller);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<ProductResponse> getAllProducts(
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "4") int limit) {
		
		return productMapper.toProductResponse(productService.getProducts(offset, limit));
	}
	
}
