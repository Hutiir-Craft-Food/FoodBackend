package com.khutircraftubackend.services;

import com.khutircraftubackend.repositories.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

}
