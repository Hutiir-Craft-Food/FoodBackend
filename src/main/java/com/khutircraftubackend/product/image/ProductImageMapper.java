package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ProductImageResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.List;

@Mapper(componentModel = SPRING)
public interface ProductImageMapper {

    ProductImageResponse.Image toDto(ProductImageEntity entity);

    default ProductImageResponse toResponseDto(List<ProductImageEntity> entities) {
        return new ProductImageResponse(
                entities.stream()
                        .map(this::toDto)
                        .toList()
        );
    }
}
