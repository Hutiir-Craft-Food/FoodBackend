package com.khutircraftubackend.product.price.repo;

import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPriceEntity, Long> {
}
