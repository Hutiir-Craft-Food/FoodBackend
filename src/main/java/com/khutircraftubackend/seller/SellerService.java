package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import com.khutircraftubackend.seller.exception.seller.SellerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
                .orElseThrow(() -> new SellerNotFoundException("User is not a valid Seller"));

        return SellerMapper.INSTANCE.toSellerResponse(seller);
    }

    public void createSeller(RegisterRequest request, UserEntity user) {
        SellerEntity seller = SellerMapper.INSTANCE.SellerDTOToSeller(request.details());
        seller.setUser(user);
        sellerRepository.save(seller);
    }

    public SellerEntity getCurrentSeller() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return sellerRepository.findByUserEmail(email)
                .orElseThrow(() -> new SellerNotFoundException("User is not a valid Seller"));
    }

    public SellerEntity getSellerId(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found"));
    }
}