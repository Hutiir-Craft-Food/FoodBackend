package com.khutircraftubackend.auth;

import com.khutircraftubackend.seller.Seller;
import jakarta.persistence.*;
import lombok.*;

/*
CREATE TABLE users (
                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                       jwt VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE
);
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String jwt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Seller seller;

    private boolean enabled;

    private String confirmationToken; // Код підтвердження, який відправляється на пошту


}
