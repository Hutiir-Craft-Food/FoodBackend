package com.khutircraftubackend.blogadvertising;

import jakarta.validation.constraints.NotBlank;

public record BlogRequest(

        @NotBlank(message = "Image URL is mandatory")
        String image,

        @NotBlank(message = "Name is mandatory")
        String name
) {
}
