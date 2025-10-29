package com.khutircraftubackend.product.response;

import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.seller.SellerResponse;

import java.util.List;

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
        CategoryResponse category,
        List<ProductPriceResponse> prices
) {
}
