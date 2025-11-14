package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductImageMapper {

     @Mapping(target = "productId", source = "product.id")
     @Mapping(target = "links", expression = "java(mapVariantsToImageLinks(entity.getVariants()))")
     ProductImageDTO toProductImageDto(ProductImageEntity entity);

     default ImageLinks mapVariantsToImageLinks(List<ProductImageVariant> variants) {
          if (variants == null || variants.isEmpty()) {
               return null;
          }

          ImageLinks.ImageLinksBuilder builder = ImageLinks.builder();

          for (ProductImageVariant variant : variants) {
               switch (variant.getTsSize()) {
                    case THUMBNAIL -> builder.thumbnail(variant.getLink());
                    case SMALL -> builder.small(variant.getLink());
                    case MEDIUM -> builder.medium(variant.getLink());
                    case LARGE -> builder.large(variant.getLink());
               }
          }

          return builder.build();
     }
}