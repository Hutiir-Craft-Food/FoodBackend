package com.khutircraftubackend.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProductAccessException extends RuntimeException{
    
    public ProductAccessException(String message) {
        super(message);
    }
}
