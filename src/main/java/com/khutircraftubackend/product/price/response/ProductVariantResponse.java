package com.khutircraftubackend.product.price.response;

import com.khutircraftubackend.seller.SellerResponse;

import java.math.BigDecimal;

public record ProductVariantResponse(
		Product product,
		Price price,
		Unit unit,
		SellerResponse seller
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

}
