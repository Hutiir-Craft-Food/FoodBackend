package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Product save(Product product) {
        try {
            if(product.getCreateAt() == null) {
                product.setCreateAt(new Date());
            }
            product.setName(product.getName());
            product.setPrice(product.getPrice());
            product.setInfoSeller(product.getInfoSeller());
            product.setDescription(product.getDescription());
            product.setMeasurement(product.getMeasurement());
            productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save product", e);
        }
        return product;
    }

    public Optional<Product> showProduct(long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public void updateProduct(long id, Product productUpdate) {
        Optional<Product> updateToBeProduct = showProduct(id);
        if(updateToBeProduct.isPresent()) {
            Product product = updateToBeProduct.get();
            product.setName(productUpdate.getName());
            product.setCreateAt(productUpdate.getCreateAt());
            product.setMeasurement(productUpdate.getMeasurement());
            product.setDescription(productUpdate.getDescription());
            product.setPrice(productUpdate.getPrice());
            product.setInfoSeller(productUpdate.getInfoSeller());
        }
    }

    @Transactional
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> getProductsSortedByPrice() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    public List<Product> getProductsSortedByDate() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Page<Product> getProductsPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

}
