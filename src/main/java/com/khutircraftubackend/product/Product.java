package com.khutircraftubackend.product;

import com.khutircraftubackend.seller.Seller;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String thumbnailImage;//icon

    String image;

    boolean available;

    String description;

//    @OneToOne
//    @JoinColumn(name = "product_id")
//    Category category;

    @ManyToOne
    Seller seller;

    @CreationTimestamp
    LocalDateTime createdAt;//necessary

    @CreationTimestamp
    LocalDateTime updatedAt;//necessary
}
