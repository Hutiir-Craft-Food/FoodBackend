package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.UserEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Клас SellerEntity є моделлю продавця і відображає таблицю продавців у базі даних.
 */

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sellers")
public class SellerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company")
    private String company;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
