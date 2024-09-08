package com.khutircraftubackend.seller;

import com.khutircraftubackend.auth.UserEntity;
import com.khutircraftubackend.auth.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Клас SellerService реалізує бізнес-логіку для роботи з продавцями.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public SellerEntity getCurrentSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        UserEntity currentUser = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
        return sellerRepository.findByUser(currentUser)
                .orElseThrow(() -> new SellerNotFoundException("User is not a valid Seller"));
    }

    public SellerEntity getSellerId(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found"));
    }

    @Transactional
    public SellerDTO saveSeller(SellerDTO sellerDTO) {
        SellerEntity seller = SellerMapper.INSTANCE.SellerDTOToSeller(sellerDTO);
        SellerEntity savedSeller = sellerRepository.save(seller);
        return SellerMapper.INSTANCE.SellerToSellerDTO(savedSeller);
    }
}
