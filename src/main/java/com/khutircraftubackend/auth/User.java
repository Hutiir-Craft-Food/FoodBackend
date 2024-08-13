package com.khutircraftubackend.auth;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
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
    @Column(nullable = false)
    private Role role;

    private boolean enabled;

    // Код підтвердження, який відправляється на пошту:
    private String confirmationToken;
    // TODO: remove it from here.
    //  maybe should be moved into another repository with its own Entity
    //  consider using "Cron Scheduler" for removing expired expiration codes.

}
