package com.khutircraftubackend.cart;

import lombok.Getter;

@Getter
public class CartLimitExceededException extends RuntimeException {

    private final long currentUniqueCount;

    public CartLimitExceededException(String message, long currentUniqueCount) {
        super(message);
        this.currentUniqueCount = currentUniqueCount;
    }

}
