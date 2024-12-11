package com.khutircraftubackend.confirm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConfirmNotFoundException extends RuntimeException{

    public ConfirmNotFoundException(String message){
        super (message);
    }
}
