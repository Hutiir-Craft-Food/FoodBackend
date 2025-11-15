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
            generator = "product_image_variantss_seq")
    @SequenceGenerator(
            name = "product_image_variants_seq",
            sequenceName = "product_image_variantss_seq",
            allocationSize = 20)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ProductImageEntity image;

    @Column(name = "link", columnDefinition = "TEXT", nullable = false)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageSize tsSize;
}
