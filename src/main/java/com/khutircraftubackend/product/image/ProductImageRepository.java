package com.khutircraftubackend.product.image;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.*;

import java.util.Collection;
import java.util.List;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    @Lock(PESSIMISTIC_WRITE)
    List<ProductImageEntity> findByProductId(Long productId);

    @Query("""
        SELECT image.product.id AS productId, variant.link AS thumbnail
        FROM ProductImageEntity image
        JOIN image.variants variant
        WHERE image.product.id IN :productIds
          AND variant.tsSize = :thumbnailSize
          AND image.position = (
              SELECT MIN(firstImage.position)
              FROM ProductImageEntity firstImage
              WHERE firstImage.product.id = image.product.id
          )
        """)
    List<ProductThumbnailProjection> findFirstThumbnailByProductIdIn(
            @Param("productIds") Collection<Long> productIds,
            @Param("thumbnailSize") ImageSize thumbnailSize);

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
