package com.khutircraftubackend.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)// 409 Conflict
public class DuplicateUnitException extends RuntimeException {
    
    public DuplicateUnitException(String message) {
    
        super(message);
    }
}
