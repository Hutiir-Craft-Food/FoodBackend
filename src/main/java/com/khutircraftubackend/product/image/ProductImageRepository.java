package com.khutircraftubackend.product.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductId(Long productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ProductImageEntity p SET p.position = p.position + 1000 WHERE p.product.id = :productId")
    void shiftPositions(Long productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE ProductImageEntity p
        SET p.position = :position
        WHERE p.id = :imageId AND p.product.id = :productId
        """)
    void updatePosition(Long productId, Long imageId, int position);
}
