package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.dto.ProductDTO;
import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Product save(ProductDTO productDTO) {
        try {
            Product product = Product.builder()
                    .createAt(productDTO.createAt())
                    .name(productDTO.name())
                    .measurement(productDTO.measurement())
                    .description(productDTO.description())
                    .price(productDTO.price())
                    .infoSeller(productDTO.infoSeller())
                    .build();
            return productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save product", e);
        }
    }

    public Optional<Product> showProduct(long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public void updateProduct(long id, ProductDTO productDTO) {
        Optional<Product> updateToBeProduct = showProduct(id);
        if(updateToBeProduct.isPresent()) {
            Product product = updateToBeProduct.get();
            product.setName(productDTO.name());
            product.setCreateAt(productDTO.createAt());
            product.setMeasurement(productDTO.measurement());
            product.setDescription(productDTO.description());
            product.setPrice(productDTO.price());
            product.setInfoSeller(productDTO.infoSeller());

            productRepository.save(product);
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
