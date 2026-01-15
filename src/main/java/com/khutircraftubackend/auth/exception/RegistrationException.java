package com.khutircraftubackend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class RegistrationException extends RuntimeException {
    
    public RegistrationException(String message) {
        super(message);
    }
    
}
