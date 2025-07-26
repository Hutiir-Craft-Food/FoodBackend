package com.khutircraftubackend.product;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
public class ProductEntity {

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

    @Column (name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
