package com.khutircraftubackend.product;

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
@Table(name = "products")
public class ProductEntity {

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

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
