package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = { ImageVariantMapper.class } )
public interface ProductImageMapper {

     @Mapping(target = "productId", source = "product.id")
     @Mapping(target = "links", source = "variants")
     ProductImageDTO toProductImageDto(ProductImageEntity entity);

     List<ProductImageDTO> toProductImageDtoList(List<ProductImageEntity> entities);
}