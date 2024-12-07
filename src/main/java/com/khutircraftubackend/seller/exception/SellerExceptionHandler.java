package com.khutircraftubackend.seller.exception;

import com.khutircraftubackend.GlobalExceptionHandler;
import com.khutircraftubackend.seller.exception.seller.SellerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SellerExceptionHandler extends GlobalExceptionHandler {

    public record ErrorResponse ( String message ) {}

    @ExceptionHandler(SellerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSellerNotFoundException(SellerNotFoundException e) {
        return new ErrorResponse("Продавець не знайдений");
    }

}
