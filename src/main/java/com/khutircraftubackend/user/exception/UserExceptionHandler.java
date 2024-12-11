package com.khutircraftubackend.user.exception;

import com.khutircraftubackend.exception.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler  {

    @ExceptionHandler(UserBlockedException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public Object userBlockedException(UserBlockedException ex, HttpServletRequest request){
        return GlobalErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .error(HttpStatus.LOCKED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(UserEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object userEmailException(UserEmailException ex, HttpServletRequest request){
        return GlobalErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object userNotFoundException(UserNotFoundException ex, HttpServletRequest request){
        return GlobalErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
