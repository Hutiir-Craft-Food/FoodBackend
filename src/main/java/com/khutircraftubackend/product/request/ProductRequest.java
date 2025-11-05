package com.khutircraftubackend.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductRequest(
        @NotBlank(message = "Назва продукта не може бути порожньою")
        String name,
        
        @NotNull(message = "Наявність повинна бути вказана")
        Boolean available,
        
        @NotBlank(message = "Опис продукта не може бути порожнім")
        String description,
        String ingredients,
        String nutrition,
        String storage,
        String allergens,
        
        @NotNull(message = "Ідентифікатор категорії не може бути порожнім")
        Long categoryId
) {
}
