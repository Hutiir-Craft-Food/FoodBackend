package com.khutircraftubackend.product.image.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ProductImageDTO {
    private Long id;
    private Long productId;
    private String uid;
    private int position;
    private ImageLinks links;

    @Data
    @NoArgsConstructor
    public static class ImageLinks {
        private String thumbnail;
        private String small;
        private String medium;
        private String large;
    }
}
