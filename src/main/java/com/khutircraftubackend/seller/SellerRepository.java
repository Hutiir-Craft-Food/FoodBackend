package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Інтерфейс SellerRepository забезпечує доступ до даних продавців у базі даних.
 */

@Repository
public interface SellerRepository extends JpaRepository<SellerEntity, Long> {

    SellerEntity findByUser(UserEntity user);
}
