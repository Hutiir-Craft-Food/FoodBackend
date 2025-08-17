package com.khutircraftubackend.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUnitException extends RuntimeException {
    
    public InvalidUnitException(String message, Long unitId) {
        
        super(String.format(message, unitId));
    }
}
