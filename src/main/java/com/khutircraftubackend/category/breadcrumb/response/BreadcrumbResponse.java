package com.khutircraftubackend.category.breadcrumb.response;

import lombok.Builder;

@Builder
public record BreadcrumbResponse(
        
        Long id,
        String label,
        String url
) {
}
