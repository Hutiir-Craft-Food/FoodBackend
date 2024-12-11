package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import com.khutircraftubackend.seller.exception.SellerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Клас SellerService реалізує бізнес-логіку для роботи з продавцями.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserService userService;

    public SellerResponse getSellerInfo(Principal principal) {
        UserEntity user =userService.findByPrincipal(principal);

        SellerEntity seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new SellerNotFoundException(SellerResponseMessage.NOT_VALID));

        return SellerResponse.builder()
                .sellerName(seller.getSellerName())
                .companyName(seller.getCompanyName())
                .phoneNumber(seller.getPhoneNumber())
                .creationDate(seller.getCreationDate())
                .build();
    }

    public void createSeller(RegisterRequest request, UserEntity user) {
        SellerEntity seller = SellerEntity
                .builder()
                .sellerName(request.details().sellerName())
                .companyName(request.details().companyName())
                .phoneNumber(request.details().phoneNumber())
                .user(user)
                .build();
        sellerRepository.save(seller);
    }

    public SellerEntity getCurrentSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        UserEntity currentUser = userService.findByEmail(currentUserDetails.getUsername());
        return sellerRepository.findByUser(currentUser)
                .orElseThrow(() -> new SellerNotFoundException(SellerResponseMessage.NOT_VALID));
    }

    public SellerEntity getSellerId(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(SellerResponseMessage.NOT_FOUND));
    }

}
