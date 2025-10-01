package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.NotNull;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_NOT_BLANK;

public record ProductUnitRequest(

        @NotNull(message = UNIT_NOT_BLANK)
        String name
) {
}
