package com.khutircraftubackend.blogadvertising;

import jakarta.validation.constraints.NotBlank;

public record BlogRequest(
        
        @NotBlank(message = "Посилання на зображення є обов'язковим")
        String image,
        
        @NotBlank(message = "Назва блогу є обов'язковою")
        String name
) {
}
