package com.khutircraftubackend.validated_type.exception;

import lombok.Getter;

@Getter
public class SuspiciousFileException extends RuntimeException {

    private final String fileName;
    private final String declaredType;
    private final String detectedType;

    public SuspiciousFileException(String message, String fileName,
                                   String declaredType, String detectedType) {
        super(message);
        this.fileName = fileName;
        this.declaredType = declaredType;
        this.detectedType = detectedType;
    }
}
