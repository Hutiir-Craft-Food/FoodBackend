package com.khutircraftubackend.product;

import com.khutircraftubackend.seller.SellerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllBy(Pageable pageable);

    Optional<ProductEntity> findProductById(Long id);

    void deleteBySeller(SellerEntity seller);

    List<ProductEntity> findAllBySeller(SellerEntity currentSeller);
}
