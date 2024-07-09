package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "seller_name", referencedColumnName = "name")
    private SellerEntity seller;
}
