package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ProductImageDTO;
import com.khutircraftubackend.product.image.response.ProductImageMinimalDTO;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.*;

@Mapper(componentModel = SPRING)
public interface ProductImageMapper {

    default List<ProductImageDTO> toProductImageDto(List<ProductImageEntity> entities) {
        return ProductImageAssemble.assembleImageDto(entities);
    }

    default List<ProductImageMinimalDTO> toProductImageMinimalDto(List<ProductImageEntity> entities) {
        return ProductImageAssemble.assembleMinimalDto(entities);
    }
}