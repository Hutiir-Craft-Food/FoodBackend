package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ProductImageUtil {

    public static ImageLinks getLinks(Map.Entry<CompoundImageKey, List<ProductImageEntity>> entry) {
        ImageLinks links = new ImageLinks();
        for (ProductImageEntity imageEntity : entry.getValue()) {
            switch (imageEntity.getTsSize()) {
                case THUMBNAIL -> links.setThumbnail(imageEntity.getLink());
                case SMALL -> links.setSmall(imageEntity.getLink());
                case MEDIUM -> links.setMedium(imageEntity.getLink());
                case LARGE -> links.setLarge(imageEntity.getLink());
            }
        }
        return links;
    }

    public static Map<CompoundImageKey, List<ProductImageEntity>> groupByImageKey(List<ProductImageEntity> imageEntities) {
        return imageEntities.stream()
                .collect(Collectors.groupingBy(imageEntity ->
                        new CompoundImageKey(
                                imageEntity.getProduct().getId(),
                                imageEntity.getUid(),
                                imageEntity.getPosition()
                        )));
    }

    public static Function<Map.Entry<CompoundImageKey, List<ProductImageEntity>>, ProductImageDTO> getEntryToDtoMapper() {
        return (entry)-> {
            CompoundImageKey key = entry.getKey();
            ImageLinks links = getLinks(entry);

            return ProductImageDTO.builder()
                    .productId(key.getProductId())
                    .uid(key.getProductUid())
                    .position(key.getPosition())
                    .links(links)
                    .build();
        };
    }

}
