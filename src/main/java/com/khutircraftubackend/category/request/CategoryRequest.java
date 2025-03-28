package com.khutircraftubackend.category.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CategoryRequest(
		@NotBlank(message = "Category name cannot be blank")
		@Pattern(regexp = "^[\\p{L}\\d\\s_-]+$", message = "Назва містить недопустимі символи.")
		String name,
		@NotBlank(message = "Description cannot be blank")
        String description,
        Long parentCategoryId
) {
}
