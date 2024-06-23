package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author uriiponomarenko 27.05.2024
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //@Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    //@Column(nullable = false)
    private BigDecimal price;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    //@Column(columnDefinition = "TEXT", nullable = false)
    private String infoSeller;

    //@Column(columnDefinition = "TEXT", nullable = false)
    private String measurement;

    @ManyToOne
    private User user;

}
