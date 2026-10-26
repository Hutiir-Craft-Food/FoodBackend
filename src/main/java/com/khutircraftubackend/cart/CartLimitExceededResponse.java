package com.khutircraftubackend.cart;

public record CartLimitExceededResponse(
        String status,
        String message,
        long currentUniqueCount
) {
}
