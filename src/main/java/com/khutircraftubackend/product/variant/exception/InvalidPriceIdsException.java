package com.khutircraftubackend.product.variant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPriceIdsException extends RuntimeException {

    public InvalidPriceIdsException(String message) {
        super(message);
    }
}
