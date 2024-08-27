package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.UserEntity;
import com.khutircraftubackend.auth.UserRepository;
import com.khutircraftubackend.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Клас SellerService реалізує бізнес-логіку для роботи з продавцями.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public SellerResponse getInfoSeller(Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Користувач з таким email не знайдений"));

        SellerEntity seller = sellerRepository.findByUser(user);
        return SellerResponse.builder()
                .sellerName(seller.getSellerName())
                .companyName(seller.getCompanyName())
                .phoneNumber(seller.getPhoneNumber())
                .creationDate(seller.getCreationDate())
                .email(user.getEmail())
                .build();
    }
}
