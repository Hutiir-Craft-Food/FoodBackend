package com.khutircraftubackend.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageProcessingException extends RuntimeException{

    public ImageProcessingException(String message){
        super(message);
    }
}