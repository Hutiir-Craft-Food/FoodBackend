package com.khutircraftubackend.product.image.response;

import lombok.*;

@Builder
public record PublicProductImageDTO(
    int position,
    ImageLinks links
){}

