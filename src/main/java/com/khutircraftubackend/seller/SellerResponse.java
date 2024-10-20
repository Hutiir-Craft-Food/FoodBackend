package com.khutircraftubackend.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellerResponse(
		Long id,
        String sellerName,
        String companyName,
        String phoneNumber,
        String email,
        LocalDateTime creationDate
) {
}
