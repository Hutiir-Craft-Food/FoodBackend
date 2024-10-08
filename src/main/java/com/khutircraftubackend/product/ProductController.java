package com.khutircraftubackend.product;

import com.khutircraftubackend.product.request.ProductUpdateRequest;
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

    @PostMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductEntity> createProduct(
            @ModelAttribute("name") String name,
            @ModelAttribute("thumbnailImage") MultipartFile thumbnailImage,
            @ModelAttribute("image") MultipartFile image,
            @ModelAttribute("available") Boolean available,
            @ModelAttribute("description") String description,
            @ModelAttribute("categoryId") Long categoryId,
            @ModelAttribute("sellerId") Long sellerId) throws IOException {

        ProductEntity newProduct = productService.createProduct(name, thumbnailImage, image, available, description, sellerId, categoryId);

        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    public ResponseEntity<ProductEntity> patchProduct(@PathVariable Long productId,
                                                      @ModelAttribute ProductUpdateRequest request,
                                                      @RequestParam("thumbnailImage") MultipartFile thumbnailImage,
                                                      @RequestParam("images") MultipartFile image) throws IOException {

        ProductEntity patchedProduct = productService.patchProduct(productId, request, thumbnailImage, image);

        return ResponseEntity.ok(patchedProduct);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER') and @productService.canModifyProduct(#productId)")
    public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long productId,
                                                       @ModelAttribute ProductUpdateRequest request,
                                                       @RequestParam("thumbnailImage") MultipartFile thumbnailImage,
                                                       @RequestParam("images") MultipartFile image) throws IOException {
        ProductEntity updatedProduct = productService.updateProduct(productId, request, thumbnailImage, image);

        return ResponseEntity.ok(updatedProduct);
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
