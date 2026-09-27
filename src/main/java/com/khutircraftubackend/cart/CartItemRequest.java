package com.khutircraftubackend.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CartItemRequest(

		@Min(1)
		@Max(100000)
		int quantity
) {
}
