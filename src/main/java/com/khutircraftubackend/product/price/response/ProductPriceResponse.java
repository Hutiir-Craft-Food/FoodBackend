package com.khutircraftubackend.product.price.response;

import java.math.BigDecimal;

public record ProductPriceResponse(
        Long id,
        BigDecimal price,
        int qty,
        ProductUnitResponse unit
) {
}
