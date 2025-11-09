package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.MultipleProductsException;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
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

        // though, the current logic in the ProductImage domain
        // doesn't expect to have images for multiple products in imageEntities
        // it's still technically possible to have such a case here
        // implementing CompoundImageKey helps us deal with cases like that
        Map<CompoundImageKey, List<ProductImageEntity>> imageEntitiesGroupedByPosition = groupByImageKey(imageEntities);
        if (imageEntitiesGroupedByPosition.size() > 1) {
            throw new MultipleProductsException(ProductImageResponseMessages.ERROR_IMAGES_PER_MULTIPLE_PRODUCTS);
        }

        Function<Map.Entry<CompoundImageKey,
                List<ProductImageEntity>>, ProductImageDTO> entryToDtoMapper = getEntryToDtoMapper();

        return imageEntitiesGroupedByPosition.entrySet().stream()
                .map(entryToDtoMapper)
                .sorted(Comparator.comparing(ProductImageDTO::getPosition))
                .toList();
    }
}