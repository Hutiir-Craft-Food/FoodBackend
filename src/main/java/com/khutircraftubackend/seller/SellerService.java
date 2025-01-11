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
    private final SellerMapper sellerMapper;

    public SellerResponse getSellerInfo(Principal principal) {
        UserEntity user = userService.findByPrincipal(principal);

        SellerEntity seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new SellerNotFoundException(SellerResponseMessage.NOT_VALID));

        return sellerMapper.toSellerResponse(seller);
    }

    public void createSeller(RegisterRequest request, UserEntity user) {
        SellerEntity seller = sellerMapper.SellerDTOToSeller(request.details());
        seller.setUser(user);
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