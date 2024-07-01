package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
}
