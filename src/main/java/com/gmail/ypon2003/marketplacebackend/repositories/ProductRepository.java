package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
