package com.khutircraftubackend.repositories;

import com.khutircraftubackend.models.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<SellerEntity, Long> {
    SellerEntity findByConfirmationCode(String confirmationCode);
    SellerEntity findByEmail(String email);
}
