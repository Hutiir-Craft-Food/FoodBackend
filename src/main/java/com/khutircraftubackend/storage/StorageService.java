package com.khutircraftubackend.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    
    String upload(MultipartFile multipartFile) throws IOException;
    
    void deleteByUrl(String fileUrl) throws IOException;
}
