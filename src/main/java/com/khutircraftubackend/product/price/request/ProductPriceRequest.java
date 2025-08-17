package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductPriceRequest(
        
        Long id,
        @DecimalMin(value = "0.01", message = "Ціна повинна бути більшою за 0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,
        
        @Min(value = 1, message = "Кількість повинна бути більшою за 0")
        int qty,
        
        @NotNull(message = "Ідентифікатор міри вагм не може бути порожнім")
        Long unitId
) {
}
