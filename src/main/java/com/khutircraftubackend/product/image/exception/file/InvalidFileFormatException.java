package com.khutircraftubackend.product.image.exception.file;

import java.io.IOException;

public class InvalidFileFormatException extends IOException {
    public InvalidFileFormatException(String message) {
        super(message);
    }
}
