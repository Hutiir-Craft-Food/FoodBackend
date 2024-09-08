package com.khutircraftubackend.product.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileConverterService {
    private final FileUploadService fileUploadService;

    public String convert(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        return fileUploadService.uploadImage(file);
    }
}
