package com.khutircraftubackend.category.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
		Long id,
		String name,
		String description,
		Long parentId,
		String iconUrl
) {
}
