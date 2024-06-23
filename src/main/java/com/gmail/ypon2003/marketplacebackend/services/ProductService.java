package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.models.Product;
import com.gmail.ypon2003.marketplacebackend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    @Cacheable("products")
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    @Transactional
    public Product save(Product product) {
        product.setName(product.getName());
        product.setPrice(product.getPrice());
        product.setInfoSeller(product.getInfoSeller());
        product.setDescription(product.getDescription());
        product.setMeasurement(product.getMeasurement());
        return productRepository.save(product);
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
            product.setDescription(productUpdate.getDescription());
            product.setPrice(productUpdate.getPrice());
            product.setInfoSeller(product.getInfoSeller());
            product.setMeasurement(product.getMeasurement());
        }
    }

    @Transactional
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }
    public List<Product> searchProductsByName(String name) {

        return productRepository.findByName(name);
    }

    public List<Product> getProductsSortedByPrice() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    public List<Product> getProductsSortedByDate() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

}
