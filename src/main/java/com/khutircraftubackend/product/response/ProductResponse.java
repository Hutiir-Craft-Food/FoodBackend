package com.khutircraftubackend.product.response;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.seller.SellerEntity;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String thumbnailImageUrl,
        String imageUrl,
        boolean available,
        String description,
        SellerEntity seller,
        CategoryEntity category
) {
}
