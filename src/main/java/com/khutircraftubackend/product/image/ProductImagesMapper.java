package com.khutircraftubackend.product.image;

import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static com.khutircraftubackend.product.image.ProductImagesResponse.ImageResponse;

import java.util.List;

@Mapper(componentModel = SPRING)
public interface ProductImagesMapper {

    ImageResponse toDto(ProductImagesEntity entity);

    default ProductImagesResponse toResponseDto(List<ProductImagesEntity> entities) {
        return new ProductImagesResponse(
                entities.stream()
                        .map(this::toDto)
                        .toList()
        );
    }
}
