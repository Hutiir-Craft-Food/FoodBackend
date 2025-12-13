package com.khutircraftubackend.product.image.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateImagePositionException extends RuntimeException{

    public DuplicateImagePositionException(String message){
        super(message);
    }
}
