package com.khutircraftubackend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserBlockedException extends RuntimeException{
    public UserBlockedException(String message){
        super (message);
    }
}
