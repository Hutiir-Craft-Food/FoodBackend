package com.khutircraftubackend.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByUserId(Long userId);

    Optional<CartItemEntity> findByUserIdAndProductPriceId(Long userId, Long productPriceId);

    @Modifying
    @Query(value = """
        INSERT INTO cart_items (user_id, product_price_id, quantity, created_at, updated_at)
        VALUES (:userId, :productPriceId, :quantity, now(), now())
        ON CONFLICT ON CONSTRAINT uq_cart_items_user_product_price
        DO UPDATE SET quantity = :quantity, updated_at = now()
        """, nativeQuery = true)
    void upsertCartItem(@Param("userId") Long userId,
                        @Param("productPriceId") Long productPriceId,
                        @Param("quantity") int quantity);

}
