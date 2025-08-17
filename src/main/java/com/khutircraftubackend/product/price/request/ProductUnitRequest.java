package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.khutircraftubackend.product.exception.ProductResponseMessage.UNIT_NOT_BLANK;

public record ProductUnitRequest(
        
        @NotBlank(message = UNIT_NOT_BLANK)
        @Size(max = 10, message = "Міра виміру не може містити більше 10 символів")
        String name
) {
}
