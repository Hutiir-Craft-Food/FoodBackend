package com.khutircraftubackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/*
CREATE TABLE users (
                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                       jwt VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE
);
 */

/**
 * Клас User є моделлю користувача і відображає таблицю користувачів у базі даних.
 */

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String jwt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Seller seller;

    public void setSeller(Seller seller) {
        this.seller = seller;
        if(seller != null) {
            seller.setUser(this);
        }
    }

    @Column(nullable = false)
    @NotBlank(message = "Підтвердження паролю не може бути порожнім")
    @Size(min = 8, max = 30, message = "Підтвердження паролю має містити від 8 до 30 символів")
    @Pattern(regexp = "^[^!@#$%^&*()_+=]*$", message = "Підтвердження паролю не може містити спеціальні символи !@#$%^&*()_+=")
    private String confirmPassword;

    private boolean enabled;

    private String confirmationCode; // Код підтвердження, який відправляється на пошту

}
