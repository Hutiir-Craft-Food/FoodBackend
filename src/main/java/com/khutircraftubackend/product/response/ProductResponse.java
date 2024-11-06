package com.khutircraftubackend.product.response;

import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.seller.response.SellerResponse;
import lombok.Builder;

@Builder
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
