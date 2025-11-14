package com.khutircraftubackend.product.image;

import com.khutircraftubackend.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product_image_variants")
public class ProductImageVariant extends Auditable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_image_variants_seq")
    @SequenceGenerator(
            name = "product_image_variants_seq",
            sequenceName = "product_image_variants_seq",
            allocationSize = 4)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private ProductImageEntity image;

    @Column(name = "link", columnDefinition = "TEXT", nullable = false)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageSize tsSize;
}
