package com.khutircraftubackend.storage.exception.storage;

import java.io.IOException;

public class FileNotFoundException extends IOException {
	public FileNotFoundException(String message) {
        super(message);
    }
}
