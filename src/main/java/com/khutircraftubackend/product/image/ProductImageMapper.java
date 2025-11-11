package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ProductImageDTO;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.khutircraftubackend.product.image.ProductImageUtil.groupByImageKey;
import static com.khutircraftubackend.product.image.ProductImageUtil.getEntryToDtoMapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductImageMapper {

    default List<ProductImageDTO> toProductImageDto(List<ProductImageEntity> imageEntities) {
        if (Objects.isNull(imageEntities) || imageEntities.isEmpty()) {
            return Collections.emptyList();
        }

        Map<CompoundImageKey, List<ProductImageEntity>> imageEntitiesGroupedByPosition = groupByImageKey(imageEntities);

        Function<Map.Entry<CompoundImageKey, List<ProductImageEntity>>,
                ProductImageDTO> entryToDtoMapper = getEntryToDtoMapper();

        return imageEntitiesGroupedByPosition.entrySet().stream()
                .map(entryToDtoMapper)
                .sorted(Comparator.comparing(ProductImageDTO::getPosition))
                .toList();
    }
}