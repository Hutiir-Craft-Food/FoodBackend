package com.khutircraftubackend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CloudinaryServiceImpl implements StorageService {
	
	private final Cloudinary cloudinary;
	
	@Override
	public String upload(MultipartFile multipartFile) throws IOException {
		
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}
		Map<?, ?> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
				ObjectUtils.asMap("resource_type", "auto"));
		
		return uploadResult.get("url").toString();
	}
	
	@Override
	public void deleteById(String publicId) throws IOException {
		cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
	}
}
