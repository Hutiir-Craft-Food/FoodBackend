package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
