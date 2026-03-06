package com.khutircraftubackend.product.image.exception;

public class ImageProcessingException extends Exception {

    public ImageProcessingException(String message) {
        super(message);
    }
    public ImageProcessingException(String message, Throwable cause) {
        super(message);
    }
}