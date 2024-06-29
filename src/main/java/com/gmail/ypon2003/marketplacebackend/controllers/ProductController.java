package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dto.ProductDTO;
import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author uriiponomarenko 28.05.2024
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products management", description = "Operations related to products management")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get of list all products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products")
    @GetMapping("/all-products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> productList = productService.findAll();
        return ResponseEntity.ok(productList);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID of the product to be retrieved", required = true)
            @PathVariable Long id) {
        Optional<Product> product = productService.showProduct(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/create-product")
    @Operation(summary = "Creating new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {

        Product createdProduct = productService.save(productDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> updateProduct(
            @Parameter(description = "ID of the person to be updated", required = true)
            @PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
        productService.updateProduct(id, productDTO);
        return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleting products by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct (
            @Parameter(description = "ID of the product to be deleted", required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search of products from name", description = "Return the status")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam("name") String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/sorted")
    @Operation(summary = "Sorted of products from price or date created", description = "Creates a list of sorted products and returns the status")
    public ResponseEntity<List<Product>> getProductsSorted(@RequestParam("sortBy") String sortBy) {
        List<Product> products;
        if(sortBy.equals("price")) {
            products = productService.getProductsSortedByPrice();
        } else {
            products = productService.getProductsSortedByDate();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping
    @Operation(summary = "Pageable of pages", description = "Creates pageable of pages, starting with 0 pages and 10 goods per page, return status")
    public ResponseEntity<Page<Product>> getProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productService.getProductsPage(pageable);
        return ResponseEntity.ok(productsPage);
    }
}
