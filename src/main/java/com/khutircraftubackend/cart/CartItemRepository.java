package com.khutircraftubackend.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    @Query("""
            select item.productPrice.id as productPriceId, item.quantity as quantity
            from CartItemEntity item
            where item.user.id = :userId
            order by item.id
            """)
    List<CartItemProjection> findItemsByUserId(@Param("userId") Long userId);

    Optional<CartItemEntity> findByUser_IdAndProductPrice_Id(Long userId, Long productPriceId);

    interface CartItemProjection {
        Long getProductPriceId();

        int getQuantity();
    }
}
