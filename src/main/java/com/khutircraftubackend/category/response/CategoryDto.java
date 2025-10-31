package com.khutircraftubackend.category.response;

import lombok.Builder;

@Builder
public record CategoryDto (
        Long id,
        String name
){
}
