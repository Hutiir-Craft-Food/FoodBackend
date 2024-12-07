package com.khutircraftubackend.delivery;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record DeliveryMethodRequest(
		
		@NonNull
		String name,
		
		@NonNull
		BigDecimal cost,
		
		@NonNull
		String estimatedDeliveryTime,
		
		boolean isActive,
		
		@NonNull
        DeliveryMethodProvider deliveryProvider
) {
}
