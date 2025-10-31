package com.khutircraftubackend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khutircraftubackend.storage.exception.CloudStorageException;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CloudinaryService implements StorageService {
    private final Cloudinary cloudinary;

    @Override
    public String upload(byte[] bytes, String originalFilename) {
        if (bytes == null || bytes.length == 0) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }
        try {
            Map<?, ?> options = ObjectUtils.asMap("resource_type", "auto");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(bytes, options);

            return uploadResult.get("url").toString();

        } catch (IOException e) {
            throw new CloudStorageException(String.format(StorageResponseMessage.ERROR_SAVE, e));
        }
    }

    @Override
    public String upload(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }

        try {
            return upload(multipartFile.getBytes(), null);
        } catch (IOException e) {
            throw new CloudStorageException(String.format(StorageResponseMessage.ERROR_SAVE, e));
        }
    }

    @Override
    public void deleteByUrl(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new CloudStorageException(String.format(StorageResponseMessage.ERROR_DELETE, publicId, e));
        }
    }
}