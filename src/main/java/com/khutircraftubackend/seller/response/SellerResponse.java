package com.khutircraftubackend.seller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)//Hides field in JSON response if its value is null
@Builder
public record SellerResponse(
		Long id,
        String sellerName,
        String companyName,
        String phoneNumber,
        LocalDateTime creationDate
) {
}
