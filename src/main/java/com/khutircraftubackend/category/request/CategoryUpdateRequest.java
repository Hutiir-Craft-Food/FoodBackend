package com.khutircraftubackend.category.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record CategoryUpdateRequest (
        String name,
        String description,
        Long parentCategoryId,
        MultipartFile iconFile
) {
    }
