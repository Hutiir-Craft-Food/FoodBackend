package com.khutircraftubackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Інтерфейс UserRepository забезпечує доступ до даних користувачів у базі даних.
 */

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
