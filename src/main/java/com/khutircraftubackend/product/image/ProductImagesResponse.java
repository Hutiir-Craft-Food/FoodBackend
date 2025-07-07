package com.khutircraftubackend.product.image;

import java.util.List;

public record ProductImagesResponse(
        List<ImageResponse> images
){
    public record ImageResponse (
            String uid,
            String link,
            ImageSizes tsSize,
            int position)
    {}
}


