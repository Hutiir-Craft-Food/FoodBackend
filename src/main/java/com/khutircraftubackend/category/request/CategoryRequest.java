package com.khutircraftubackend.category.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CategoryRequest(
		@NotBlank(message = "Category name cannot be blank")
		String name,
		@NotBlank(message = "Description cannot be blank")
        String description,
        Long parentCategoryId
) {
}
