package com.khutircraftubackend.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryCreationException extends RuntimeException {
    
    public CategoryCreationException(String message) {
        super(message);
    }
    
}
