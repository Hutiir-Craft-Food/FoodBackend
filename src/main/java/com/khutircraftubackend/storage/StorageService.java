package com.khutircraftubackend.storage;

public interface StorageService {

    String upload(byte[] bytes, String originalFileName);

    void deleteByUrl(String fileUrl);
}
