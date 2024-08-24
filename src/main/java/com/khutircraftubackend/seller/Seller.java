package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.User;
import com.khutircraftubackend.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Клас Seller є моделлю продавця і відображає таблицю продавців у базі даних.
 */

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;

    private String tax_code;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;
}
