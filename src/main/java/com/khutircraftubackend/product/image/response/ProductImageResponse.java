package com.khutircraftubackend.product.image.response;

import com.khutircraftubackend.product.image.ImageSizes;

import java.util.List;

public record ProductImageResponse(
        List<Image> images
){
    public record Image(
            String uid,
            String link,
            ImageSizes tsSize,
            int position)
    {}
}


