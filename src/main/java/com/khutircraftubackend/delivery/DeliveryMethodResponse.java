package com.khutircraftubackend.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryMethodResponse(
		Long id,
		String name,
		BigDecimal cost,
		String estimatedDeliveryTime,
		boolean isActive,
		DeliveryMethodProvider deliveryMethodProvider
) {
}
