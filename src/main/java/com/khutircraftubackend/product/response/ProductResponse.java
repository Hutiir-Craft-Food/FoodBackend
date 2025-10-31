package com.khutircraftubackend.product.response;

import com.khutircraftubackend.category.response.CategoryDto;
import com.khutircraftubackend.product.image.response.ProductImageMinimalDTO;
import com.khutircraftubackend.product.price.response.ProductPriceResponse;
import com.khutircraftubackend.seller.SellerResponse;

import java.util.List;

public record ProductResponse(
        
        Long id,
        String name,
        boolean available,
        String description,
        SellerResponse seller,
        CategoryDto category,
        List<ProductPriceResponse> prices,
        List<ProductImageMinimalDTO> images
) {
}
