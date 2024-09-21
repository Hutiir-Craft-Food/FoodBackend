package com.khutircraftubackend.category.request;

import lombok.Builder;

@Builder
public record CategoryCreateRequest(
        String name,
        String description,
        Long parentCategoryId
) {
}
