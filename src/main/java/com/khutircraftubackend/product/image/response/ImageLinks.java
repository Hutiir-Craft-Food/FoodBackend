package com.khutircraftubackend.product.image.response;

import lombok.*;

@Builder
public record ImageLinks (
     String thumbnail,
     String small,
     String medium,
     String large
){}