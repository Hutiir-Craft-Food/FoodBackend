package com.khutircraftubackend.marketing;

import com.khutircraftubackend.marketing.MarketingCampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaignEntity, Long> {
}
