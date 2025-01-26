package com.khutircraftubackend.seller.exception;

import com.khutircraftubackend.exception.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class SellerExceptionHandler {

    @ExceptionHandler(SellerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleSellerNotFoundException(SellerNotFoundException ex, HttpServletRequest request) {
        return GlobalErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}