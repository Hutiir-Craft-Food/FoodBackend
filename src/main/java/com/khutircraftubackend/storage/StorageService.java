package com.khutircraftubackend.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

	String upload(MultipartFile multipartFile) throws IOException;
	
	void deleteById(String publicId) throws IOException;
}
