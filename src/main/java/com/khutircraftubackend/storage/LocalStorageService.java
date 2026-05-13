package com.khutircraftubackend.storage;

import com.khutircraftubackend.storage.exception.DirectoryCreationException;
import com.khutircraftubackend.storage.exception.FileNotFoundException;
import com.khutircraftubackend.storage.exception.InvalidArgumentException;
import com.khutircraftubackend.storage.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RequiredArgsConstructor
@Slf4j
public class LocalStorageService implements StorageService {
    private final String basePath;
    private final String publicBaseUrl;

    @Override
    public String upload(byte[] fileBytes, String originalFileName) {
        Path uploadPath = Paths.get(basePath);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new DirectoryCreationException(
                    String.format(StorageResponseMessage.ERROR_CREATE_DIRECTORY, uploadPath));
        }

        String safeName = Paths.get(originalFileName).getFileName().toString();
        Path filePath = uploadPath.resolve(safeName);
        
        try {
            Files.copy(new ByteArrayInputStream(fileBytes), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(StorageResponseMessage.ERROR_SAVE);
        }

        String relativePath = uploadPath.relativize(filePath).normalize().toString();
        return publicBaseUrl + "/" + relativePath;
    }

    public Resource getResource(String fileName) {

        Path filePath = Paths.get(basePath).resolve(fileName).normalize();

        if (Files.notExists(filePath)) {
            throw new FileNotFoundException(String.format(StorageResponseMessage.FILE_NOT_FOUND, filePath));
        }

        return new FileSystemResource(filePath);
    }

    @Override
    public void deleteByUrl(String fileUrl) {

        if (!fileUrl.startsWith(publicBaseUrl)) {
            throw new InvalidArgumentException(
                    String.format(StorageResponseMessage.INVALID_ARGUMENT, fileUrl));
        }

        String relativePath = fileUrl.substring(publicBaseUrl.length() + 1);
        Path filePath = Paths.get(basePath).resolve(relativePath);

        if (Files.notExists(filePath)) {
            throw new FileNotFoundException(
                    String.format(StorageResponseMessage.FILE_NOT_FOUND, relativePath));
        }
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new StorageException(StorageResponseMessage.ERROR_DELETE_LOCAL);
        }
    }

}