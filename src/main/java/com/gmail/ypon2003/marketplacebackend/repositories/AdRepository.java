package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByNameContaining(String name);
}
