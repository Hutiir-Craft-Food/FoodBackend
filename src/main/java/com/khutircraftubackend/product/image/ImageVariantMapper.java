package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ImageLinks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring",  unmappedTargetPolicy = IGNORE)
public interface ImageVariantMapper {

    default ImageLinks toImageLinks(List<ProductImageVariant> variants) {
        if (variants == null) return null;

        return ImageLinks.builder()
                .thumbnail(findLink(variants, ImageSize.THUMBNAIL))
                .small(findLink(variants, ImageSize.SMALL))
                .medium(findLink(variants, ImageSize.MEDIUM))
                .large(findLink(variants, ImageSize.LARGE))
                .build();
    }

    default String findLink(List<ProductImageVariant> variants, ImageSize size) {
        if (variants == null) return null;
        return variants.stream()
                .filter(v -> v.getTsSize() == size)
                .map(ProductImageVariant::getLink)
                .findFirst()
                .orElse(null);
    }
}