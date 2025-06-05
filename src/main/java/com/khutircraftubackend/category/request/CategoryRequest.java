package com.khutircraftubackend.category.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.LinkedHashSet;

import static com.khutircraftubackend.category.CategoryResponseMessage.NOT_VALID_SYMBOL;

@Builder
public record CategoryRequest (
		@NotBlank(message = "Category name cannot be blank")
		@Pattern(regexp = "^[\\p{L}\\d-_\\s]+$", message = NOT_VALID_SYMBOL)
		String name,

		@NotBlank(message = "Description cannot be blank")
		String description,

		@Nullable
		Long parentCategoryId,

		@Nullable
		LinkedHashSet<String> keywords
) { }
