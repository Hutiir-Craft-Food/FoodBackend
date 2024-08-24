package com.khutircraftubackend.product;

import com.khutircraftubackend.seller.Seller;
import lombok.Builder;

@Builder
public record ProductCreateRequest(

        String name,
        String thumbnailImage,
        String image,
        boolean available,
        String description,
        Seller seller
) {
}
