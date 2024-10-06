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
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtExceptionHandler {

    @ExceptionHandler({JWTVerificationException.class,
                        TokenExpiredException.class
    })
        public String handleJWTException(RuntimeException e) {
        log.error("JWT Exception: {}", e.getMessage());
        return "Помилка аутентифікації";
    }

}
