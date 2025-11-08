package com.khutircraftubackend.product.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.common.view.Public;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.request.ProductPriceDTO;
import com.khutircraftubackend.seller.SellerResponse;
import lombok.Builder;

import java.util.List;

@Builder
@JsonView(Public.class)
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
        List<ProductPriceDTO> prices,
        List<ProductUnitEntity> units,
        List<ProductImageDTO> images
) {
}
