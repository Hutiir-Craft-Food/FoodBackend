package com.khutircraftubackend.storage;

import com.khutircraftubackend.storage.exception.FileNotFoundException;
import com.khutircraftubackend.storage.exception.InvalidArgumentException;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
public class LocalStorageService implements StorageService {
    private final String basePath;
    private final String apiPath;
    
    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidFileFormatException("Файл не надано або він порожній");
        }
        String fileName = UUID.randomUUID().toString();
        
        Path uploadPath = Paths.get(basePath);
        Files.createDirectories(uploadPath);
        
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = "";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String newFileName = fileName + extension;
        
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(multipartFile.getInputStream(), filePath);
        
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();
        String relativeUriStr = apiPath + uploadPath
                .relativize(filePath).normalize();
        
        return UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .path(relativeUriStr)
                .build()
                .toUriString();
        
    }
    
    public Resource getResource(String fileName) throws IOException {
        
        Path filePath = Paths.get(basePath).resolve(fileName).normalize();
        
        if (Files.notExists(filePath)) {
            throw new FileNotFoundException("Файл з URL " + fileName + " не знайдено.");
        }
        
        return new FileSystemResource(filePath);
    }
    
    @Override
    public void deleteByUrl(String fileUrl) throws IOException {
        
        if (!fileUrl.contains(apiPath)) {
            throw new InvalidArgumentException(fileUrl + " -URL не відповідає шаблону для зображення");
        }
        
        String[] parts = fileUrl.split(Pattern.quote(apiPath + "/"));
        
        if (parts.length < 2) {
            throw new InvalidArgumentException("Некоректний URL: " + fileUrl);
        }
        
        String filePathStr = parts[1];
        Path filePath = Paths.get(basePath, filePathStr).normalize();
        
        if (Files.notExists(filePath)) {
            throw new FileNotFoundException("Файл з іменем " + filePath + " не знайдено.");
        }
        Files.delete(filePath);
    }
    
}
