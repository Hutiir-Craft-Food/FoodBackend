package com.khutircraftubackend.product.image;

import java.util.List;

public record ProductImagesResponse(
        List<ImageResponse> images
){
    public record ImageResponse (
            Long id,
            String link,
            ImageSizes tsSize,
            int position)
    {}
}


