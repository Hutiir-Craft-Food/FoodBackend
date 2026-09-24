package com.khutircraftubackend.cart;

import com.khutircraftubackend.audit.Auditable;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_cart_items_user_product_price",
                columnNames = {"user_id", "product_price_id"}
        )
)
public class CartItemEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_price_id", nullable = false)
    private ProductPriceEntity productPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

}
