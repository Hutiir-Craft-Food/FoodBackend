package com.khutircraftubackend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Enumerated
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String jwt;
}
