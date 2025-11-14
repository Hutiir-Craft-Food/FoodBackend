package com.khutircraftubackend.product.image.response;

import lombok.*;

import java.util.List;

//@Getter
//@Setter
@Builder
//@AllArgsConstructor
//@NoArgsConstructor
public record ProductImageDTO(
     Long id,
     Long productId,
     String uid,
     int position,
     ImageLinks links
){}

//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ProductImageLinks implements ImageLinks {
//        private String thumbnail;
//        private String small;
//        private String medium;
//        private String large;
//    }
