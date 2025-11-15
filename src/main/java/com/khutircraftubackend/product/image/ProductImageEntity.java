package com.khutircraftubackend.product.image;

import com.khutircraftubackend.audit.Auditable;
import com.khutircraftubackend.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product_imagess",
        indexes = {
        @Index(name = "idx_product_images_product_id", columnList = "product_id"),
        @Index(name = "idx_product_images_uid", columnList = "product_id, uid"),
        @Index(name = "idx_product_images_position", columnList = "product_id, position"),
})
public class ProductImageEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "product_imagess_seq")
    @SequenceGenerator( name = "product_images_seq",
    sequenceName = "product_imagess_seq",
    allocationSize = 4)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "position", nullable = false)
    private int position;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImageVariant> variants;
}