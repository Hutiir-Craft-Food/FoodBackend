package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Клас Seller є моделлю продавця і відображає таблицю продавців у базі даних.
 */

@Entity
@Table (name = "seller")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column (name = "creation_date")
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate(){
        creationDate = LocalDateTime.now();
    }
}