package com.khutircraftubackend.category.path.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryPathItem(
        
        Long id,
        String name,
        List<CategoryPathItem> children
) {
    public static CategoryPathItem leaf(Long id, String name) {
        
        return new CategoryPathItem(id, name, null);
    }
    
    public static CategoryPathItem withChild(Long id, String name, CategoryPathItem child) {
        
        return new CategoryPathItem(id, name, List.of(child));
    }
}
