package com.khutircraftubackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.khutircraftubackend.user.Role.ADMIN;

/**
 * Інтерфейс UserRepository забезпечує доступ до даних користувачів у базі даних.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<String> findAllEmailsByRole(Role role);
}
