package com.khutircraftubackend.services;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.mapper.SellerMapper;
import com.khutircraftubackend.models.Seller;
import com.khutircraftubackend.repositories.SellerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Клас SellerService реалізує бізнес-логіку для роботи з продавцями.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;

    @Transactional
    public SellerDTO saveSeller(SellerDTO sellerDTO) {
        Seller seller = SellerMapper.INSTANCE.SellerDTOToSeller(sellerDTO);
        Seller savedSeller = sellerRepository.save(seller);
        return SellerMapper.INSTANCE.SellerToSellerDTO(savedSeller);
    }
}
