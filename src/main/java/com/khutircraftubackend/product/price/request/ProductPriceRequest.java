package com.khutircraftubackend.product.price.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductPriceRequest(
        
        @NotNull
        @Valid
        List<ProductPriceDTO> prices
) {
}
