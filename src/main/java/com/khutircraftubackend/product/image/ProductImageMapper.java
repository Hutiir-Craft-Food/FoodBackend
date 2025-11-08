package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductImageMapper {

    default List<ProductImageDTO> toProductImageDto(List<ProductImageEntity> imageEntities) {
        if (Objects.isNull(imageEntities) || imageEntities.isEmpty()) {
            return Collections.emptyList();
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
                        .links(new ImageLinks())
                        .build();

                dtoMap.put(key, dto);
            }

            ImageLinks links = dto.getLinks();

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