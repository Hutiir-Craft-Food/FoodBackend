package com.khutircraftubackend.storage;

import com.khutircraftubackend.exception.FileReadingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String upload(byte[] bytes, String originalFileName);

    default String upload(MultipartFile file){
        byte[] bytes;

        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new FileReadingException(e.getMessage());
        }

        return upload(bytes, file.getOriginalFilename());
    }

    void deleteByUrl(String fileUrl);
}
