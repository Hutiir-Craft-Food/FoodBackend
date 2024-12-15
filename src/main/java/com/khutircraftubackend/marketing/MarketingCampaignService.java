package com.khutircraftubackend.marketing;

import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketingCampaignService {

    private final MarketingCampaignRepository marketingCampaignRepository;

    public void createReceiveAdvertising(RegisterRequest request, UserEntity user) {
        MarketingCampaignEntity advertising = MarketingCampaignEntity
                .builder()
                .user(user)
                .isSubscribed(request.isReceiveAdvertising())
                .build();

        marketingCampaignRepository.save(advertising);
    }
}
