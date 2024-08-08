package com.khutircraftubackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Інтерфейс UserRepository забезпечує доступ до даних користувачів у базі даних.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByConfirmationToken(String confirmationToken);
    boolean existsByEmail(String email);
}
