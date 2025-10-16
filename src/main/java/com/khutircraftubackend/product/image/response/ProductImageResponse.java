package com.khutircraftubackend.product.image.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductImageResponse(
        List<ProductImageDTO> images
) {}