package com.khutircraftubackend.product.image.request;

import java.util.List;
import java.util.Map;

public record ProductImagesResponse(
        List<ImageResponse> images
){
    public record ImageResponse (
            String uid,
            int position,
            Map<String, String> sizes)
    {}
}


