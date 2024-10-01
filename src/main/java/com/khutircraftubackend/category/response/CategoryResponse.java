package com.khutircraftubackend.category.response;

import lombok.Builder;

@Builder
public record CategoryResponse (
        String name,
        String description,
        Long parentId,
        String iconUrl
) {
}
