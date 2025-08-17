package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.NotBlank;

public record ProductUnitRequest(
        
        @NotBlank(message = "Міра виміру не може бути порожньою")
        String name
) {
}
