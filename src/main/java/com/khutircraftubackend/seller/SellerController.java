package com.khutircraftubackend.seller;

import com.khutircraftubackend.seller.response.SellerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Клас SellerController обробляє запити, пов'язані з продавцями.
 */

@RestController
@RequestMapping("/v1/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerMapper sellerMapper;

    @GetMapping ("/info")
    public SellerResponse getSellerInfo(Principal principal){
        
        return sellerService.getSellerInfo(principal);
    }

}
