package com.khutircraftubackend.category.path.response;

import lombok.Builder;

@Builder
public record CategoryPathItem(
        
        Long id,
        String name
) {
}
