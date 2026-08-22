package com.khutircraftubackend.product.variant.response;

import java.math.BigDecimal;

public record ProductVariantDTO(
        Product product,
        Price price,
        Unit unit,
        Seller seller
) {
    public record Product(
            Long id,
            String name,
            boolean available,
            Images images
    ) {
    }

    public record Images(String thumbnail) {
    }

    public record Price(
            Long id,
            BigDecimal value,
            int qty
    ) {
    }

    public record Unit(
            Long id,
            String name
    ) {
    }

    public record Seller(
            Long id,
            String name
    ) {
    }
}
