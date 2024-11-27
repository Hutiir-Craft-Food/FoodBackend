package com.khutircraftubackend.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column (name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "email", unique = true, nullable = false)
    private String email;

    @Column (name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column (name = "role", nullable = false)
    private Role role;

    @Column (name = "enabled")
    private boolean enabled;

    @Column( name = "confirmed")
    private boolean confirmed;

    @Column (name = "creation_date")
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate(){
        creationDate = LocalDateTime.now();
    }
}