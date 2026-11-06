package com.khutircraftubackend.cart.exception;

public class CartBusyException extends RuntimeException {

	public  CartBusyException(String message, Throwable cause) {
		super(message, cause);
	}

}
