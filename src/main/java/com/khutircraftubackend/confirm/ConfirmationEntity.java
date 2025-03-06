package com.khutircraftubackend.confirm;

import com.khutircraftubackend.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmationEntity {

    @Id
    @Column (name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( name = "confirmation_token")
    private String confirmationToken;

    @OneToOne
    @JoinColumn (name = "user_id")
    private UserEntity user;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}