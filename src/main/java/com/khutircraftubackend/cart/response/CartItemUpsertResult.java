package com.khutircraftubackend.cart.response;

public record CartItemUpsertResult(CartItemResponse item, boolean created) {
}
