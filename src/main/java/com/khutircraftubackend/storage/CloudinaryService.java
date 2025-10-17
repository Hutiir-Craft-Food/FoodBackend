package com.khutircraftubackend.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khutircraftubackend.storage.StorageResponseMessage;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CloudinaryService implements StorageService {
    private final Cloudinary cloudinary;

    @Override
    public String upload(byte[] bytes, String originalFilename) throws IOException {
        if (bytes == null || bytes.length == 0) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }
        try {
            Map<?, ?> options = ObjectUtils.asMap("resource_type", "auto");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(bytes, options);

            return uploadResult.get("url").toString();

        } catch (IOException e) {
            // TODO: review exception handling here.
            //  is it necessary to catch IOException and re-throw it ?
            throw new IOException(String.format(StorageResponseMessage.ERROR_SAVE, e));
        }
    }

    @Override
    public String upload(MultipartFile multipartFile) throws IOException {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }

        return upload(multipartFile.getBytes(), null);
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