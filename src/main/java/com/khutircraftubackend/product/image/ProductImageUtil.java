package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.MultipleProductsException;
import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ProductImageUtil {

    public static ImageLinks getLinks(
            Map.Entry<CompoundImageKey, List<ProductImageEntity>> entry
    ) {
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

    public static Map<CompoundImageKey, List<ProductImageEntity>> groupByImageKey(
            List<ProductImageEntity> imageEntities
    ) {
        Map<CompoundImageKey, List<ProductImageEntity>> imageEntitiesGroupedByPosition = imageEntities.stream()
                .collect(Collectors.groupingBy(imageEntity ->
                        new CompoundImageKey(
                                imageEntity.getProduct().getId(),
                                imageEntity.getUid(),
                                imageEntity.getPosition()
                        )));

        if (imageEntitiesGroupedByPosition.size() > 1) {
            // though, the current logic in the ProductImage domain
            // doesn't expect to have images for multiple products in imageEntities
            // it's still technically possible to have such a case here
            // implementing CompoundImageKey helps us deal with cases like that
            throw new MultipleProductsException(ProductImageResponseMessages.ERROR_IMAGES_PER_MULTIPLE_PRODUCTS);
        }

        return imageEntitiesGroupedByPosition;
    }

    public static Function<Map.Entry<CompoundImageKey, List<ProductImageEntity>>,
            ProductImageDTO> getEntryToDtoMapper() {
        return (entry) -> {
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
