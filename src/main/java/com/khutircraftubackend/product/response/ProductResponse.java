package com.khutircraftubackend.product.response;

import com.khutircraftubackend.category.response.CategoryDto;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.seller.SellerResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductResponse(
        
        Long id,
        String name,
        boolean available,
        String description,
        String ingredients,
        String nutrition,
        String storage,
        String allergens,
        SellerResponse seller,
        CategoryDto category,
        List<ProductPriceResponse> prices,
        List<ProductUnitEntity> units
) {
}
