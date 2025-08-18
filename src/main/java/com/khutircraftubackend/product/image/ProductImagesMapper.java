package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.request.ProductImagesResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = SPRING)
public interface ProductImagesMapper {

    default ProductImagesResponse toResponseDto(List<ProductImagesEntity> entities) {
        return new ProductImagesResponse(
                groupAndMapImages(entities)
        );
    }

    private List<ProductImagesResponse.ImageResponse> groupAndMapImages(List<ProductImagesEntity> entities) {
        return entities.stream()
                .collect(Collectors.groupingBy(
                        ProductImagesEntity::getUid,
                        Collectors.groupingBy(ProductImagesEntity::getPosition)
                ))
                .entrySet().stream()
                .flatMap(this::mapUidGroup)
                .sorted(Comparator.comparingInt(ProductImagesResponse.ImageResponse::position))
                .toList();
    }

    private Stream<ProductImagesResponse.ImageResponse> mapUidGroup(
            Map.Entry<String, Map<Integer, List<ProductImagesEntity>>> uidGroup) {
        return uidGroup.getValue().entrySet().stream()
                .map(positionGroup -> createImageResponse(
                        uidGroup.getKey(),
                        positionGroup.getKey(),
                        positionGroup.getValue()
                ));
    }

    private ProductImagesResponse.ImageResponse createImageResponse(
            String uid, int position, List<ProductImagesEntity> sizeVariants) {
        return new ProductImagesResponse.ImageResponse(
                uid,
                position,
                mapSizeVariants(sizeVariants)
        );
    }

    private Map<String, String> mapSizeVariants(List<ProductImagesEntity> sizeVariants) {
        return sizeVariants.stream()
                .collect(Collectors.toMap(
                        variant -> variant.getTsSize().name().toLowerCase(),
                        ProductImagesEntity::getLink
                ));
    }
}
