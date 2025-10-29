package com.khutircraftubackend.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPaginationParameterException extends RuntimeException{
    
    public InvalidPaginationParameterException(String message) {
        super(message);
    }
}
