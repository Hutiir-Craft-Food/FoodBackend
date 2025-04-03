package com.khutircraftubackend.seller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SellerException extends RuntimeException {

    public SellerException(String message){
        super(message);
    }
}