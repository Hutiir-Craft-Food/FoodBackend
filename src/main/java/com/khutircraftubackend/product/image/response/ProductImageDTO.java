package com.khutircraftubackend.product.image.response;

import lombok.*;

@Builder
public record ProductImageDTO(
     Long id,
     Long productId,
     String uid,
     int position,
     ImageLinks links
){}