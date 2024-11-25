package com.khutircraftubackend.product.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
	
	String uploadResource(MultipartFile multipartFile) throws IOException;
	
	void deleteResourceById(String publicId) throws IOException;
}
