package com.khutircraftubackend.jwt.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class JwtExceptionHandler {
    
    @ExceptionHandler({
            JWTVerificationException.class,
            TokenExpiredException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleJwtException(RuntimeException e) {
        
        log.error("JWT Exception: {}", e.getMessage());
        
    return "Authentication error";
    }
}
