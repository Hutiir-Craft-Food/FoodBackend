package com.khutircraftubackend.cart.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartMergeItemRequest(

		@NotNull
		Long productPriceId,

		@Min(1)
		@Max(100000)
		int quantity
) {
}
