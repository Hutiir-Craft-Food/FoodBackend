package com.khutircraftubackend.product.price.repo;

import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnitEntity, Long> {
}
