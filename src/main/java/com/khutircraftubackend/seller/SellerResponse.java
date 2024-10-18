package com.khutircraftubackend.seller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SellerResponse(
		Long id,
        String sellerName,
        String companyName,
        String phoneNumber,
        String email,
        LocalDateTime creationDate
) {
}
