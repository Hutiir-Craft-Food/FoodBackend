package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);

    Boolean existsByEmail(String email);
}
