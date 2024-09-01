package com.khutircraftubackend.seller;

public class SellerNotFoundException extends RuntimeException{
    public SellerNotFoundException(String message) {
        super(message);
    }
}
