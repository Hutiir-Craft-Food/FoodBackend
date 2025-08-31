package com.khutircraftubackend.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePriceException extends RuntimeException {
    
    public DuplicatePriceException(String message) {
        
        super(message);
    }
}
