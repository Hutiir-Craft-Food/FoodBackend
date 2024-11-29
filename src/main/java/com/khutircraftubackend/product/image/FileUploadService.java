package com.khutircraftubackend.product.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {
	
	private final Cloudinary cloudinary;
	
	public String uploadImage(MultipartFile multipartFile) throws IOException {
		
		Map<?, ?> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
				ObjectUtils.asMap("resource_type", "auto"));
		
		return uploadResult.get("url").toString();
	}
	
	public void deleteCloudinaryById(String publicId) throws IOException {
		
		cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
	}
	
	public String extractPublicId(String url) {
		
		String[] parts = url.split("/");
		
		return parts[parts.length - 1].split("\\.")[0];
	}
}
