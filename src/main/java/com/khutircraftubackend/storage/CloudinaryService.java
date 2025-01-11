package com.khutircraftubackend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khutircraftubackend.storage.exception.FileDeleteException;
import com.khutircraftubackend.storage.exception.FileUploadException;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CloudinaryService implements StorageService {
	private final Cloudinary cloudinary;
	
	@Override
	public String upload(MultipartFile multipartFile) {
		
		// TODO:
		//  consider annotating `multipartFile` with @NotNull
		//  process separately any exception that thrown with @NotNull validation
		//  check here only if multipartFile.isEmpty()
		if (multipartFile == null || multipartFile.isEmpty()) {
			log.error("Invalid or empty file");
			throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
		}

		try {
			Map<?, ?> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
					ObjectUtils.asMap("resource_type", "auto"));
			
			return uploadResult.get("url").toString();
			
		} catch (IOException e) {
			log.error("Failed to save file: {}", multipartFile.getName());
			throw new FileUploadException(String.format(StorageResponseMessage.ERROR_SAVE, e));
		}
	}
	
	@Override
	public void deleteByUrl(String publicId) {
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		} catch (IOException e) {
			// TODO: consider adding some logging here
			throw new FileDeleteException(String.format(StorageResponseMessage.ERROR_DELETE, publicId, e));
		}
	}
}