package com.khutircraftubackend.product.image.response;

import lombok.*;

@Builder
public record ProductImageDTO(
     Long id,
     Long productId,
     int position,
     ImageLinks links
){}