package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.exception.ImageValidationException;
import com.khutircraftubackend.product.image.response.ImageLinks;
import com.khutircraftubackend.product.image.response.ProductImageDTO;
import com.khutircraftubackend.product.image.response.ProductImageMinimalDTO;
import com.khutircraftubackend.product.image.response.ProductImageResponseMessages;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class ProductImageAssemble {

    public static List<ProductImageDTO> assembleImageDto(List<ProductImageEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            throw new ImageValidationException(ProductImageResponseMessages.ERROR_LIST_EMPTY);
        }

        Map<String, ProductImageDTO> dtoMap = new LinkedHashMap<>();

        for (ProductImageEntity entity : entities) {
            String key = generateKey(entity);

            ProductImageDTO dto = dtoMap.get(key);

            if (dto == null) {
                dto = ProductImageDTO.builder()
                        .id(entity.getId())
                        .productId(entity.getProduct().getId())
                        .uid(entity.getUid())
                        .position(entity.getPosition())
                        .links(new ProductImageDTO.ProductImageLinks())
                        .build();

                dtoMap.put(key, dto);
            }


            setLink(dto.getLinks(), entity);
        }

        return new ArrayList<>(dtoMap.values());
    }

    public static List<ProductImageMinimalDTO> assembleMinimalDto(List<ProductImageEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();

        Map<String, ProductImageMinimalDTO> dtoMap = new LinkedHashMap<>();

        for (ProductImageEntity entity : entities) {
            String key = generateKey(entity);

            ProductImageMinimalDTO dto = dtoMap.get(key);

            if (dto == null) {
                dto = ProductImageMinimalDTO.builder()
                        .position(entity.getPosition())
                        .links(new ProductImageMinimalDTO.ProductImageLinks())
                        .build();

                dtoMap.put(key, dto);
            }

            setLink(dto.getLinks(), entity);
        }

        return new ArrayList<>(dtoMap.values());
    }

    private static String generateKey(ProductImageEntity entity) {
        return String.format("%s-%s-%d",
                entity.getProduct().getId(),
                entity.getUid(),
                entity.getPosition());
    }

    private static void setLink(ImageLinks links, ProductImageEntity entity) {
        String link = entity.getLink();
        if (link == null || entity.getTsSize() == null) return;

        switch (entity.getTsSize()) {
            case SMALL -> links.setSmall(link);
            case MEDIUM -> links.setMedium(link);
            case LARGE -> links.setLarge(link);
            case THUMBNAIL -> links.setThumbnail(link);
        }
    }
}
