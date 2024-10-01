package com.khutircraftubackend.product.request;

import com.khutircraftubackend.seller.SellerEntity;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ProductCreateRequest(

        String name,
        MultipartFile thumbnailImage,
        MultipartFile image,
        Boolean available,
        String description,
        SellerEntity seller,
        Long categoryId
) {
}
