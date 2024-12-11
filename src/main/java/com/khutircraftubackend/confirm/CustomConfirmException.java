package com.khutircraftubackend.confirm;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomConfirmException extends RuntimeException{

    public CustomConfirmException(String message){
        super (message);
    }
}
