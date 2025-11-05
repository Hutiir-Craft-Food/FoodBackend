package com.khutircraftubackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileReadingException extends RuntimeException{

    public FileReadingException(String message){
        super(message);
    }
}
