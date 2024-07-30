package com.khutircraftubackend.controllers;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.services.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Клас SellerController обробляє запити, пов'язані з продавцями.
 */

@RestController
@RequestMapping("/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping
    ResponseEntity<SellerDTO> createSeller(@Valid @RequestBody SellerDTO sellerDTO) {
        SellerDTO createdSeller = sellerService.saveSeller(sellerDTO);
        return ResponseEntity.ok(createdSeller);
    }

}
