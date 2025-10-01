package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.NotBlank;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_NOT_BLANK;

public record ProductUnitRequest(

        @NotBlank(message = UNIT_NOT_BLANK)
        String name
) {
}
