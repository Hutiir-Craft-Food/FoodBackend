package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ImageLinks;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface ImageVariantMapper {

    default ImageLinks toImageLinks(List<ProductImageVariant> variants) {
        if (variants == null) return null;

        var map = variants.stream()
                .collect(Collectors.toMap(ProductImageVariant::getTsSize, ProductImageVariant::getLink));

        return ImageLinks.builder()
                .thumbnail(map.get(ImageSize.THUMBNAIL))
                .small(map.get(ImageSize.SMALL))
                .medium(map.get(ImageSize.MEDIUM))
                .large(map.get(ImageSize.LARGE))
                .build();
    }
}