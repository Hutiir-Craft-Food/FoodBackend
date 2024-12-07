package com.khutircraftubackend.product;

import com.khutircraftubackend.Auditable;
import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class ProductEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "thumbnail_image")
    String thumbnailImageUrl;

    @Column(name = "image")
    String imageUrl;

    @Column(name = "available")
    boolean available;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    SellerEntity seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    CategoryEntity category;
    
}
