package com.khutircraftubackend.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(ProductCreateRequest request) {
        Product newProduct = ProductMapper.toEntity(request);
        return productRepository.save(newProduct);
    }

    

    public List<Product> getProducts(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return productRepository.findAllBy(pageable);
    }
}
