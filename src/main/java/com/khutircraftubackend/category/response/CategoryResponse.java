package com.khutircraftubackend.category.response;

import lombok.Builder;

import java.util.Set;

@Builder
public record CategoryResponse (
		Long id,
		String name,
		String description,
		Long parentCategoryId,
		String iconUrl,
		Set<String> keywords
) {
}
