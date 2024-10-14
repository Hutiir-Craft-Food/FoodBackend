package com.khutircraftubackend.product;

import com.khutircraftubackend.product.request.ProductCreateRequest;
import com.khutircraftubackend.product.request.ProductUpdateRequest;
import com.khutircraftubackend.product.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

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
            @PathVariable Long productId,
            @Valid @ModelAttribute ProductUpdateRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ProductEntity patchedProduct = productService.patchProduct(productId, request, thumbnailImage, image);

        return productMapper.toProductResponse(patchedProduct);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(
            @PathVariable Long productId,
            @Valid @ModelAttribute ProductUpdateRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ProductEntity updatedProduct = productService.updateProduct(productId, request, thumbnailImage, image);

        return productMapper.toProductResponse(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) throws IOException {

        productService.deleteProduct(productId);

    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasRole('SELLER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllProductsForCurrentSeller() throws IOException {

        productService.deleteAllProductsForCurrentSeller();

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "4") int limit) {

        return productMapper.toProductResponse(productService.getProducts(offset, limit));
    }

}
