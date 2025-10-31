package com.khutircraftubackend.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(byte[] bytes, String originalFileName);

    String upload(MultipartFile multipartFile);

    void deleteByUrl(String fileUrl);
}
