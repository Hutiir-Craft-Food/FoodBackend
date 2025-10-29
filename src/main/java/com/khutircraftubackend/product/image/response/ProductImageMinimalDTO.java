package com.khutircraftubackend.product.image.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageMinimalDTO {
    private int position;
    private ProductImageLinks links;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageLinks implements ImageLinks {
        private String thumbnail;
        private String small;
        private String medium;
        private String large;
    }
}

