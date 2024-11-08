package com.khutircraftubackend.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return id.equals(user.id) && email.equals(user.email) && role == user.role;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email, role);
    }
}