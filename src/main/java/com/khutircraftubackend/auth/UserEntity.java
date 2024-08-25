package com.khutircraftubackend.auth;

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

    @Column (name = "creation_date")
    private LocalDateTime creationDate;

    @Column( name = "confirmation_token")
    private String confirmationToken;

  // TODO: remove it from here.
  //  maybe should be moved into another repository with its own Entity
  //  consider using "Cron Scheduler" for removing expired expiration codes.

    @PrePersist
    protected void onCreate(){
        creationDate = LocalDateTime.now();
    }


}
