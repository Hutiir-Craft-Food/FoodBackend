package com.khutircraftubackend.product.search.response;

public record ProductSearchItemResponse(
        Long id,
        String name,
        String thumbnailImage,
        boolean available,
        Long categoryId,
        String categoryName
) {
}
