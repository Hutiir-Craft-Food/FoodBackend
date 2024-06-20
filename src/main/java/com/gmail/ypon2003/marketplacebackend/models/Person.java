package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * @author uriiponomarenko 27.05.2024
 */
@Entity
@Data
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long person_id;

    @Column(name = "person_name")
    private String name;

    @Column(name = "person_last_name")
    private String lastName;

    @Column(name = "person_email")
    @Email
    private String email;

    @Column(name = "person_phone_number")
    private String phoneNumber;

    @Column(name = "person_password")
    private String password;

    @Column(name = "person_role")
    private String role;

    @ManyToMany
    @JoinTable(name = "user_favorite_ads", joinColumns = @JoinColumn(name = "favorite_id"))
    private List<Ad> favorites = new ArrayList<>();

    public void addToFavorites(Ad ad) {
    }
}
