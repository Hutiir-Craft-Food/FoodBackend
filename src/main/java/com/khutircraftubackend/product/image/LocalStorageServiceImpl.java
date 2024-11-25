package com.khutircraftubackend.product.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@Primary
public class LocalStorageServiceImpl implements ResourceService{
	
	private final String basePath;
	
	@Autowired
	public LocalStorageServiceImpl(@Value("${storage.local.base-path:uploads/}") String basePath) {
		this.basePath = basePath;
	}
	
	@Override
	public String uploadResource(MultipartFile multipartFile) throws IOException {
		
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}
		
		Path uploadPath = Paths.get(basePath).toAbsolutePath();
		Files.createDirectories(uploadPath);
		
		Path filePath = uploadPath.resolve(Objects.requireNonNull(multipartFile.getOriginalFilename()));
		Files.copy(multipartFile.getInputStream(), filePath);
		
		return filePath.toString();
	}
	
	@Override
	public void deleteResourceById(String publicId) throws IOException {
		
		Path filePath = Paths.get(publicId);
		Files.delete(filePath);
		
	}
}
