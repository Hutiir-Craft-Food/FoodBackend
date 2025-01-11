package com.khutircraftubackend.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	
	String upload(MultipartFile multipartFile) throws Exception;
	
	void deleteByUrl(String fileUrl) throws Exception;
}
