package com.khutircraftubackend.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductRequest(
        @NotBlank(message = "Product name cannot be blank")
        String name,

        @NotNull(message = "Availability must be specified")
        Boolean available,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Category ID cannot be null")
        Long categoryId
) { }
