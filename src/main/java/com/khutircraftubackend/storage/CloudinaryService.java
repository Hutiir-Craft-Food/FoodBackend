package com.khutircraftubackend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khutircraftubackend.storage.exception.StorageException;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;

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
            throw new StorageException(StorageResponseMessage.ERROR_SAVE);
        }
    }

    @Override
    public void deleteByUrl(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new StorageException(String.format(StorageResponseMessage.ERROR_DELETE, publicId, e));
        }
    }
}