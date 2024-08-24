package com.khutircraftubackend.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreateRequest request) {
        Product newProduct = productService.createProduct(request);

        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "4") int limit) {
        return productService.getProducts(offset, limit);
    }


    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        List<String> categories = new ArrayList<>();
        categories.add("blah");
        return ResponseEntity.ok(categories);
    }
}
