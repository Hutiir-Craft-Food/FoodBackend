package com.gmail.ypon2003.marketplacebackend.repositories;

import com.gmail.ypon2003.marketplacebackend.models.UserFavoriteAds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Repository
public interface UserFavoriteAdsRepository extends JpaRepository<UserFavoriteAds, Long> {
}
