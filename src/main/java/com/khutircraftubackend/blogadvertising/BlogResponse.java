package com.khutircraftubackend.blogadvertising;

import lombok.Builder;

import java.util.List;

@Builder
public record BlogResponse(
        
        List<BlogItem> items
) {
    public record BlogItem(
            
            String id,
            String image,
            String name
    ) {}
}
