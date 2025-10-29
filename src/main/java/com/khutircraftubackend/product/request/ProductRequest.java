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

        @NotNull(message = "Склад продукту повинен бути вказаний.")
        String ingredients,

        @NotNull(message = "Поживна цінність повинна бути вказана.")
        String nutrition,

        @NotNull(message = "Умови зберігання повинні бути вказані.")
        String storage,

        @NotNull(message = "Інформація про алергени повинна бути вказана.")
        String allergens,
        
        @NotNull(message = "Ідентифікатор категорії не може бути порожнім")
        Long categoryId
) {
}
