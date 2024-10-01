package com.khutircraftubackend.product.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ProductUpdateRequest(
        String name,
        MultipartFile thumbnailImage,
        MultipartFile image,
        Boolean available,
        String description,
        Long categoryId
) {
}
