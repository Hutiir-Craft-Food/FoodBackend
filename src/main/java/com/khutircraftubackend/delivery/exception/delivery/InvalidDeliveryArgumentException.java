package com.khutircraftubackend.delivery.exception.delivery;

import lombok.Getter;

@Getter
public class InvalidDeliveryArgumentException extends RuntimeException{
	
    private final Long sellerId;
    private final Long methodId;
    public InvalidDeliveryArgumentException(String message, Long sellerId, Long methodId) {
        super(message);
        this.sellerId = sellerId;
        this.methodId = methodId;
    }
}
