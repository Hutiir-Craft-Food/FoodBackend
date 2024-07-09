package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dtos.ProductDTO;
import com.gmail.ypon2003.marketplacebackend.models.ProductEntity;
import com.gmail.ypon2003.marketplacebackend.models.SellerEntity;
import com.gmail.ypon2003.marketplacebackend.repositories.ProductRepository;
import com.gmail.ypon2003.marketplacebackend.repositories.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getCreateDate(), product.getSeller().getName()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDto) {
        ProductEntity product = new ProductEntity();
        product.setName(productDto.name());
        product.setPrice(productDto.price());
        product.setCreateDate(productDto.createDate());
        Optional<SellerEntity> sellerOptional = sellerRepository.findByName(productDto.sellerName());
        sellerOptional.ifPresent(product::setSeller);
        ProductEntity savedProduct = productRepository.save(product);
        return new ProductDTO(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice(), savedProduct.getCreateDate(), savedProduct.getSeller().getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            ProductEntity p = product.get();
            ProductDTO productDto = new ProductDTO(p.getId(), p.getName(), p.getPrice(), p.getCreateDate(), p.getSeller().getName());
            return ResponseEntity.ok(productDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDto) {
        Optional<ProductEntity> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            ProductEntity product = productOptional.get();
            product.setName(productDto.name());
            product.setPrice(productDto.price());
            product.setCreateDate(productDto.createDate());
            Optional<SellerEntity> sellerOptional = sellerRepository.findByName(productDto.sellerName());
            sellerOptional.ifPresent(product::setSeller);
            ProductEntity updatedProduct = productRepository.save(product);
            ProductDTO updatedProductDto = new ProductDTO(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getCreateDate(), updatedProduct.getSeller().getName());
            return ResponseEntity.ok(updatedProductDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}