package com.khutircraftubackend.category.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.LinkedHashSet;

import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.*;

@Builder
public record CategoryRequest (
		@NotBlank(message = CATEGORY_NAME_INVALID)
		@Size(max = 255, message = CATEGORY_NAME_BIG)
		@Pattern(regexp = "^([\\p{IsCyrillic}\\d\\s.,:;_\\-+()%&]+|[\\p{IsLatin}\\d\\s.,:;_\\-+()%&]+)$",
				 message = CATEGORY_NAME_INVALID_CHARACTERS)
		String name,

		@NotBlank(message = CATEGORY_DESCRIPTION_NOT_NULL)
		String description,

		@Nullable
		Long parentCategoryId,

		@Nullable
		LinkedHashSet<String> keywords
) {}
