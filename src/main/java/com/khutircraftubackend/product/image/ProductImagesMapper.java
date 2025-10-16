package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ProductImageDTO;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.*;

@Mapper(componentModel = SPRING)
public interface ProductImagesMapper {

    default List<ProductImageDTO> toProductImageDto(List<ProductImageEntity> imageEntities) {
        if (Objects.isNull(imageEntities) || imageEntities.isEmpty()) {
            throw new IllegalArgumentException("list images cannot be null or empty");
        }

        Map<String, ProductImageDTO> dtoMap = new HashMap<>();

        for (ProductImageEntity entity : imageEntities) {
            String key = String.format("%s-%s-%d",
                    entity.getProduct().getId(),
                    entity.getUid(),
                    entity.getPosition());

            ProductImageDTO dto = dtoMap.get(key);

            if (dto == null) {
                dto = ProductImageDTO.builder()
                        .id(entity.getId())
                        .productId(entity.getProduct().getId())
                        .uid(entity.getUid())
                        .position(entity.getPosition())
                        .links(new ProductImageDTO.ImageLinks())
                        .build();

                dtoMap.put(key, dto);
            }

            ProductImageDTO.ImageLinks links = dto.getLinks();

            if (entity.getTsSize() != null) {
                String link = entity.getLink();
                switch (entity.getTsSize()) {
                    case SMALL -> links.setSmall(link);
                    case MEDIUM -> links.setMedium(link);
                    case LARGE -> links.setLarge(link);
                    case THUMBNAIL -> links.setThumbnail(link);
                }
            }
        }
        return new ArrayList<>(dtoMap.values());
    }
}