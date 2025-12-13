package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.ImageConflictException;
import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface ImageVariantMapper {

    default ImageLinks toImageLinks(List<ProductImageVariantEntity> variants) {
        if (variants == null || variants.isEmpty()) return null;

        Long firstImageId = variants.get(0).getImage().getId();
        boolean mixed = variants.stream()
                .skip(1)
                .anyMatch(v -> !v.getImage().getId().equals(firstImageId));

        if (mixed) {
            throw new ImageConflictException(
                    ProductImageResponseMessages.ERROR_UNIQUE_CONFLICT);
        }

        var map = variants.stream()
                .collect(Collectors.toMap(ProductImageVariantEntity::getTsSize, ProductImageVariantEntity::getLink));

        return ImageLinks.builder()
                .thumbnail(map.get(ImageSize.THUMBNAIL))
                .small(map.get(ImageSize.SMALL))
                .medium(map.get(ImageSize.MEDIUM))
                .large(map.get(ImageSize.LARGE))
                .build();
    }
}