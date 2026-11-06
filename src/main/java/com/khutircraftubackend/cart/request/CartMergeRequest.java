package com.khutircraftubackend.cart.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartMergeRequest(@NotNull @Valid List<@NotNull @Valid CartMergeItemRequest> items) {

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public CartMergeRequest {
	}

	@JsonValue
	public List<CartMergeItemRequest> value() {
		return items;
	}
}
