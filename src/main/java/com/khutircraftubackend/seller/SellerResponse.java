package com.khutircraftubackend.seller;

import lombok.Builder;

@Builder
public record SellerResponse(
		Long id,
        String sellerName
) {
}
