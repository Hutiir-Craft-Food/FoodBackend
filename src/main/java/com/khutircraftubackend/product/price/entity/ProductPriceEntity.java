package com.khutircraftubackend.product.price.entity;

import com.khutircraftubackend.audit.Auditable;
import com.khutircraftubackend.product.ProductEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "product_prices")
public class ProductPriceEntity extends Auditable {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private ProductUnitEntity unit;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    @Column(name = "qty", nullable = false)
    private int qty;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPriceEntity that)) return false;
        return qty == that.qty &&
                Objects.equals(product, that.product) &&
                Objects.equals(unit, that.unit);    }
    
    @Override
    public int hashCode() {
        return Objects.hash(product, unit, qty);
    }
}
