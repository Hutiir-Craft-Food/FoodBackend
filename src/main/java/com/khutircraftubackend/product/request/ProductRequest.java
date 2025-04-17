package com.khutircraftubackend.product.request;

import com.khutircraftubackend.search.SearchResponseMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ProductRequest(
        @NotBlank(message = "Product name cannot be blank")
        @Pattern(regexp = "^[\\p{L}\\d\\s_ʼ'.,\\-]+$", message = SearchResponseMessage.NOT_VALID_SYMBOL)
        String name,
        @NotNull(message = "Availability must be specified")
        Boolean available,
        @NotBlank(message = "Description cannot be blank")
        String description,
        @NotNull(message = "Category ID cannot be null")
        Long categoryId
) {
}
