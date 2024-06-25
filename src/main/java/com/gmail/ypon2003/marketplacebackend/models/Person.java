package com.gmail.ypon2003.marketplacebackend.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
/**
 * @author uriiponomarenko 27.05.2024
 */
@Entity
@Table(name = "persons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the person.", example = "1", required = true)
    private Long person_id;

    @Schema(description = "First name of the person.", example = "John", required = true)
    private String name;

    @Schema(description = "Last name of the person.", example = "Doe", required = true)
    private String lastName;

    @Email
    @Schema(description = "Email address of the person.", example = "john.doe@example.com", required = true)
    private String email;

    @Schema(description = "Phone number of the person.", example = "+123456789", required = true)
    private String phoneNumber;

    @Schema(description = "Password of the person.", example = "password123", required = true)
    private String password;

    @Schema(description = "Role of the person.", example = "USER_ROLE", required = true)
    private String role;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of products created by the person.")
    private List<Product> products = new ArrayList<>();//поле яке керується сутністю Person

    @ManyToMany
    @JoinTable(
            name = "person_favorites",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Schema(description = "List of favorite products the person.")
    private List<Product> favorites = new ArrayList<>();

    public void addToFavorites(Product product) {
        this.favorites.add(product);
    }
}
