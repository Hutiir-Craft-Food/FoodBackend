package com.khutircraftubackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Інтерфейс UserRepository забезпечує доступ до даних користувачів у базі даних.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByConfirmationToken(String confirmationToken);
    boolean existsByEmail(String email);
}
