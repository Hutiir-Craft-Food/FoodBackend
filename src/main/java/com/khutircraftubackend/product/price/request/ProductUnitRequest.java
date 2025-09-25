package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.Size;

public record ProductUnitRequest(
        
        @Size(max = 10, message = "Міра виміру не може містити більше 10 символів")
        String name
) {
}
