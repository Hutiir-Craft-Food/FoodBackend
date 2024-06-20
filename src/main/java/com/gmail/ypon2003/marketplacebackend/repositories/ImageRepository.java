package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
