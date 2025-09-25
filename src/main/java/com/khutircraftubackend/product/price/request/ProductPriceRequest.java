package com.khutircraftubackend.product.price.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductPriceRequest(
        
        Long id,
        @DecimalMin(value = "0.01", message = "Ціна повинна бути більшою за 0.01")
        @DecimalMax(value = "100000.00", message = "Ціна не повинна бути більшою за 100000.00")
        BigDecimal price,
        
        @Positive(message = "Кількість повинна бути додатнім числом")
        @Max(value = 1000, message = "Кількість не повинна бути більшою за 1000")
        int qty,
        
        @NotNull(message = "Ідентифікатор міри вагм не може бути порожнім")
        @Positive(message = "Ідентифікатор міри ваги повинен бути додатнім числом")
        @Max(value = 1000000, message = "Ідентифікатор міри ваги не повинен бути більшим за 1000000")
        Long unitId
) {
}
