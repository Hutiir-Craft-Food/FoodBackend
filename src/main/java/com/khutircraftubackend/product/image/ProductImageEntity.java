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
        @Index(name = "idx_product_images_product_id", columnList = "product_id"),
        @Index(name = "idx_product_images_uid", columnList = "product_id, uid"),
        @Index(name = "idx_product_images_position", columnList = "product_id, position"),
})
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "product_images_seq")
    @SequenceGenerator( name = "product_images_seq",
    sequenceName = "product_images_seq",
    allocationSize = 20)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "uid")
    private String uid;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Enumerated(EnumType.STRING)
    private ImageSize tsSize;

    @Column(name = "position")
    private int position;

    @Column (name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
