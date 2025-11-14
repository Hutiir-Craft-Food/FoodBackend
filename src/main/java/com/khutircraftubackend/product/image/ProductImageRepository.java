package com.khutircraftubackend.product.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    ProductImageEntity findByProductId(Long productId);
}
