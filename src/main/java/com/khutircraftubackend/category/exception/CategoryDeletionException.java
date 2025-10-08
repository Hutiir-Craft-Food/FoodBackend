package com.khutircraftubackend.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryDeletionException extends RuntimeException{
    public CategoryDeletionException(String message) {
        super(message);
    }
}
