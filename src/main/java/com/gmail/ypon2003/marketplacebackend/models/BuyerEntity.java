package com.gmail.ypon2003.marketplacebackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class BuyerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String firstname;

    private LocalDateTime createDate;

    private String phone;

    @Column(nullable = false)
    private String password;

    @ElementCollection
    private List<Integer> products;

    @Column(nullable = false, unique = true)
    private String email;
}
