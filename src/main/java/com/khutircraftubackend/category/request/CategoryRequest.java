package com.khutircraftubackend.category.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;

@Builder
public record CategoryRequest(
		@NotBlank(message = "Category name cannot be blank")
		String name,
		@NotBlank(message = "Description cannot be blank")
        String description,
		@Nullable
        Long parentCategoryId,
		@Nullable
		Set<String> keywords
) {
}
