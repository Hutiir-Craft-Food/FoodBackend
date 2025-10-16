package com.khutircraftubackend.product.image.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO {
    private Long id;
    private Long productId;
    private String uid;
    private int position;
    private ImageLinks links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageLinks {
        private String thumbnail;
        private String small;
        private String medium;
        private String large;
    }
}