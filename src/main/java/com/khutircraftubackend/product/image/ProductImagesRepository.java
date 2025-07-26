package com.khutircraftubackend.product.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImagesRepository extends JpaRepository<ProductImagesEntity, Long> {

    List<ProductImagesEntity> findByProductId(Long productId);
}
