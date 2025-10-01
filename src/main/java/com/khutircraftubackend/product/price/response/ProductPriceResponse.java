package com.khutircraftubackend.product.price.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khutircraftubackend.product.price.entity.ProductUnitEntity;
import com.khutircraftubackend.product.price.request.ProductPriceDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductPriceResponse(
        
        List<ProductPriceDTO> prices,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ProductUnitEntity> units
) {
}
