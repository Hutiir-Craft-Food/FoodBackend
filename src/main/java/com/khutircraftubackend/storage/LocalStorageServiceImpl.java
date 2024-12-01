package com.khutircraftubackend.storage;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class LocalStorageServiceImpl implements StorageService {
	private final String basePath;

	@Override
	public String upload(MultipartFile multipartFile) throws IOException {
		
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}

		HttpServletRequest request =
				((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
						.getRequest();
		
		Path uploadPath = Paths.get(basePath);
		Files.createDirectories(uploadPath);
		
		String originalFilename = multipartFile.getOriginalFilename();
		String newFileName = UUID.randomUUID() + Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));

		Path newFilePath = uploadPath.resolve(Objects.requireNonNull(newFileName));
		Files.copy(multipartFile.getInputStream(), newFilePath);

		String relativeUriStr = LocalStorageController.API_PATH + uploadPath.relativize(newFilePath).normalize().toString().replace("\\", "/");
		return new URL(request.getScheme(), request.getServerName(), request.getServerPort(), relativeUriStr).toString();
	}
	
	@Override
	public void deleteById(String publicId) throws IOException {
		
		Path filePath = Paths.get(publicId);
		Files.delete(filePath);
		
	}
}
