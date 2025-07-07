package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.ProductEntity;
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
@Table(name = "product_images",
        indexes = {
        @Index(name = "idx_images_product_id", columnList = "product_id"),
})
public class ProductImagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "uid")
    private String uid;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Enumerated(EnumType.STRING)
    private ImageSizes tsSize;

    @Column(name = "position")
    private int position;

    @Column (name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
