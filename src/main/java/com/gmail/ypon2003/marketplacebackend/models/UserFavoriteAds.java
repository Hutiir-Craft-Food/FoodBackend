package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Entity
@Data
@Table(name = "user_favorite_ads")
public class UserFavoriteAds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "ad", joinColumns = @JoinColumn(name = "ad_id"))
    List<Ad> adList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "person", joinColumns = @JoinColumn(name = "person_id"))
    List<Person> personList = new ArrayList<>();

}
