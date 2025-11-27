package com.khutircraftubackend.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCategoryNameException extends RuntimeException {
    
    public InvalidCategoryNameException(String message) {
        
        super(message);
    }
    
}
