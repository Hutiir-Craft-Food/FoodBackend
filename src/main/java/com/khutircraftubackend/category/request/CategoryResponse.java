package com.khutircraftubackend.category.request;

import lombok.Builder;

@Builder
public record CategoryResponse (
        Long id,
        String name,
        String description,
        Long parentId,
        String iconUrl
) {
}
