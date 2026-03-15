package com.khutircraftubackend.product.image;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ImageSize {
    THUMBNAIL(64, 64, "thumbnail"),
    SMALL(324, 257, "small"),
    MEDIUM(438, 438, "medium"),
    LARGE(1080, 1920, "large");
    
    final int width;
    final int height;
    final String name;
    
}