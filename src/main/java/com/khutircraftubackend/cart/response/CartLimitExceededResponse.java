package com.khutircraftubackend.cart.response;

public record CartLimitExceededResponse(

        String status,
        String message,
        long currentUniqueCount
) {
}
