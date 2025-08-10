package com.khutircraftubackend.product.image;

import com.khutircraftubackend.product.image.response.ProductImageResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductImageMapperTest {

    private final ProductImageMapper mapper = Mappers.getMapper(ProductImageMapper.class);

    @Test
    void toDto_shouldMapEntityToDtoCorrectly() {
        ProductImageEntity entity = ProductImageEntity.builder()
                .uid("uid123")
                .link("link.jpg")
                .tsSize(ImageSizes.LARGE)
                .position(1)
                .build();

        ProductImageResponse.Image dto = mapper.toDto(entity);

        assertEquals("uid123", dto.uid());
        assertEquals("link.jpg", dto.link());
        assertEquals(ImageSizes.LARGE, dto.tsSize());
        assertEquals(1, dto.position());
    }

    @Test
    void toResponseDto_shouldMapListCorrectly() {
        List<ProductImageEntity> entities = List.of(
                ProductImageEntity.builder().uid("uid1").link("link1").tsSize(ImageSizes.LARGE).position(0).build(),
                ProductImageEntity.builder().uid("uid2").link("link2").tsSize(ImageSizes.SMALL).position(1).build()
        );

        ProductImageResponse response = mapper.toResponseDto(entities);

        assertEquals(2, response.images().size());

        assertEquals("uid1", response.images().get(0).uid());
        assertEquals("link1", response.images().get(0).link());
        assertEquals(ImageSizes.LARGE, response.images().get(0).tsSize());
        assertEquals(0, response.images().get(0).position());

        assertEquals("uid2", response.images().get(1).uid());
    }
}
