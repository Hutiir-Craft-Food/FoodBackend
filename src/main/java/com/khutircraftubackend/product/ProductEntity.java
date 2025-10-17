package com.khutircraftubackend.product;

import com.khutircraftubackend.audit.Auditable;
import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.product.price.entity.ProductPriceEntity;
import com.khutircraftubackend.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products",
        indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_description", columnList = "description")
})
public class ProductEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "available")
    private boolean available;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private SellerEntity seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    @Builder.Default
    private List<ProductPriceEntity> prices = new ArrayList<>();
    
}
