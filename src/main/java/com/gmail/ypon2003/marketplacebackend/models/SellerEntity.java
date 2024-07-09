package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class SellerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private LocalDateTime createDate;

    @Column(nullable = false)
    private String password;

    @ElementCollection
    private List<Integer> products;
}
