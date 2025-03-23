package com.khutircraftubackend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CloudinaryService implements StorageService {
    private final Cloudinary cloudinary;
    
    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            
            return uploadResult.get("url").toString();
            
        } catch (IOException e) {
            throw new IOException(String.format(StorageResponseMessage.ERROR_SAVE, e));
        }
    }
    
    @Override
    public void deleteByUrl(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new IOException(String.format(StorageResponseMessage.ERROR_DELETE, publicId, e));
        }
    }
}