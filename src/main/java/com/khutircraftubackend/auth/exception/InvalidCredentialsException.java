package com.khutircraftubackend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends AuthenticationException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
