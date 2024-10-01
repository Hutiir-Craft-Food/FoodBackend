package com.khutircraftubackend.product;

import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid
            @ModelAttribute ProductCreateRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ProductEntity newProduct = productService.createProduct(request, thumbnailImage, image);

        return productMapper.toProductResponse(newProduct);
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse patchProduct(
            @Valid
            @PathVariable Long productId,
            @ModelAttribute ProductUpdateRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ProductEntity patchedProduct = productService.patchProduct(productId, request, thumbnailImage, image);

        return productMapper.toProductResponse(patchedProduct);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(
            @Valid
            @PathVariable Long productId,
            @ModelAttribute ProductUpdateRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ProductEntity updatedProduct = productService.updateProduct(productId, request, thumbnailImage, image);

        return productMapper.toProductResponse(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) throws IOException {

        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteAllProductsForCurrentSeller() throws IOException {

        productService.deleteAllProductsForCurrentSeller();

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ProductEntity> getAllProducts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "4") int limit) {

        return productService.getProducts(offset, limit);
    }

}
