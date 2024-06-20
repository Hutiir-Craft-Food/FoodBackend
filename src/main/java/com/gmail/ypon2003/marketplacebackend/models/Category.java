package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

}
