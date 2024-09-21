package com.khutircraftubackend.category.request;

import lombok.Builder;

@Builder
public record CategoryCreateRequest(
        Long id,
        String name,
        String description,
        String iconUrl,
        Long parentCategoryId

//        List<Long> subCategoryIds,
//        List<Long> productEntityListIds
) {
}
