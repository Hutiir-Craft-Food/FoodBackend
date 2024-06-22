package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PostMapping("/add_product")
    public Product addProduct(@RequestBody Product product) {

        return productService.save(product);
    }

    @PutMapping("/product/{id}")
    public void updateProduct(@PathVariable("id") long id, @RequestBody Product product) {
        productService.updateProduct(id, product);
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct (@PathVariable("id") long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam("name") String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Product>> getProductsSorted(@RequestParam("sortBy") String sortBy) {
        List<Product> products;
        if(sortBy.equals("price")) {
            products = productService.getProductsSortedByPrice();
        } else {
            products = productService.getProductsSortedByDate();
        }
        return ResponseEntity.ok(products);
    }
}
