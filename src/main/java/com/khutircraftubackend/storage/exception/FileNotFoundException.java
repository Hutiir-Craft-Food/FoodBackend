package com.khutircraftubackend.storage.exception;

import java.io.IOException;

public class FileNotFoundException extends IOException {
	public FileNotFoundException(String message) {
        super(message);
    }
}
