package com.khutircraftubackend.product;

public class ProductMapper {
    public static ProductCreateRequest toDto(Product product) {
        return ProductCreateRequest.builder()
                .name(product.getName())
                .thumbnailImage(product.getThumbnailImage())
                .image(product.getImage())
                .available(product.isAvailable())
                .description(product.getDescription())
                .seller(product.getSeller())
                .build();
    }

    public static Product toEntity(ProductCreateRequest dto) {
        return Product.builder()
                .name(dto.name())
                .thumbnailImage(dto.thumbnailImage())
                .image(dto.image())
                .available(dto.available())
                .description(dto.description())
                .seller(dto.seller())
                .build();
    }
}
