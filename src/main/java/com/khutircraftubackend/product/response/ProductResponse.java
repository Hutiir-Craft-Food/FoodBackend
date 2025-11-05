package com.khutircraftubackend.product.response;

import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.request.ProductPriceDTO;
import com.khutircraftubackend.seller.SellerResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductResponse(
        
        Long id,
        String name,
        String thumbnailImageUrl,
        String imageUrl,
        boolean available,
        String description,
        SellerResponse seller,
        CategoryResponse category,
        List<ProductPriceDTO> prices,
        List<ProductUnitEntity> units
) {
}
