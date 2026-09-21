package com.khutircraftubackend.cart;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartItemNotFoundException extends RuntimeException{

	public CartItemNotFoundException(String message) {
		super(message);
	}

}
