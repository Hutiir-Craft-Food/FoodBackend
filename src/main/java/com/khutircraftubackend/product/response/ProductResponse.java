package com.khutircraftubackend.product.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.seller.SellerResponse;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResponse(
		Long id,
        String name,
        String thumbnailImageUrl,
        String imageUrl,
        boolean available,
        String description,
        SellerResponse seller,
        CategoryResponse category
) {
}
