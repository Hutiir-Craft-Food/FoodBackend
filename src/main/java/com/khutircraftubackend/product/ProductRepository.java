package com.khutircraftubackend.product;

import com.khutircraftubackend.seller.SellerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    @EntityGraph(attributePaths = {"seller", "category"})
    Page<ProductEntity> findAllBy(Pageable pageable);
    
    Optional<ProductEntity> findProductById(Long id);
    
    void deleteBySeller(SellerEntity seller);
    
}

