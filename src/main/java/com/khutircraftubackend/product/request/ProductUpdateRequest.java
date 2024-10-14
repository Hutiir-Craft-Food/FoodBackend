package com.khutircraftubackend.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ProductUpdateRequest(
        @NotBlank(message = "Product name cannot be blank")
        String name,
        MultipartFile thumbnailImage,
        MultipartFile image,
        @NotNull(message = "Availability must be specified")
        Boolean available,
        @NotBlank(message = "Description cannot be blank")
        String description,
        Long categoryId
) {
}
