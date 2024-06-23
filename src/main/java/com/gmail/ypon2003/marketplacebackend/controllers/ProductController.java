package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Tag(name = "Керування товарами", description = "Операції, пов'язані з керуванням товарами")
@RestController
@RequestMapping("/api/products")
@AllArgsConstructor

public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Отримання списку з усіма товарами", description = "Повернення списку товарів")
    @GetMapping()
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @Operation(summary = "Отримання товару із БД по id")
    @GetMapping("/show_id")
    public void showProduct(Long id) {
        productService.showProduct(id);
    }

    @Operation(summary = "Додавання нових товарів в БД")
    @PostMapping("/add_product")
    public Product addProduct(@RequestBody Product product) {

        return productService.save(product);
    }

    @Operation(summary = "Редагування існуючих товарів в БД")
    @PutMapping("/product/{id}")
    public void updateProduct(@PathVariable("id") long id, @RequestBody Product product) {
        productService.updateProduct(id, product);
    }

    @Operation(summary = "Видалення існуючих товарів в БД")
    @DeleteMapping("/product/{id}")
    public void deleteProduct (@PathVariable("id") long id) {
        productService.deleteProduct(id);
    }

    @Operation(summary = "Пошук товарів за назвою", description = "Повернення статусу запиту")
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam("name") String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Сортування товарів за ціною або за датою додавання", description = "Повернення статусу запиту")
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
