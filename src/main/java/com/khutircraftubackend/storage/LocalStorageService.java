package com.khutircraftubackend.storage;

import com.khutircraftubackend.exception.httpstatus.NotFoundException;
import com.khutircraftubackend.storage.exception.InvalidArgumentException;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import com.khutircraftubackend.storage.exception.StorageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class LocalStorageService implements StorageService {
    private final String basePath;
    private static final String API_PREFIX = LocalStorageController.API_PATH + "/";

    @Override
    public String upload(byte[] fileBytes, String originalFileName) {
        Path uploadPath = Paths.get(basePath);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(newFileName);
        try {
            Files.copy(new ByteArrayInputStream(fileBytes), filePath);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }

        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();
        String relativeUriStr = API_PREFIX + uploadPath
                .relativize(filePath).normalize();

        return UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .path(relativeUriStr)
                .build()
                .toUriString();
    }

    @Override
    public String upload(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }

        String originalFileName = multipartFile.getOriginalFilename();
        try {
            return upload(multipartFile.getBytes(), originalFileName);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
    }

    public Resource getResource(String fileName) {

        Path filePath = Paths.get(basePath).resolve(fileName).normalize();

        if (Files.notExists(filePath)) {
            throw new NotFoundException(String.format(StorageResponseMessage.FILE_NOT_FOUND, filePath));
        }

        return new FileSystemResource(filePath);
    }

    @Override
    public void deleteByUrl(String fileUrl) {

        if (!fileUrl.contains(API_PREFIX)) {
            throw new InvalidArgumentException(String.format(StorageResponseMessage.INVALID_ARGUMENT, fileUrl));
        }

        String relativePath = extractRelativePath(fileUrl);
        Path filePath = Paths.get(basePath).resolve(relativePath);

        if (Files.notExists(filePath)) {
            throw new NotFoundException(String.format(StorageResponseMessage.FILE_NOT_FOUND, relativePath));
        }
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new StorageException(StorageResponseMessage.ERROR_DELETE_LOCAL);
        }
    }

    private String extractRelativePath(String fileUrl) {
        int index = fileUrl.indexOf(LocalStorageService.API_PREFIX);
        if (index == -1) {
            throw new InvalidArgumentException(
                    String.format(StorageResponseMessage.INVALID_ARGUMENT, fileUrl)
            );
        }
        return fileUrl.substring(index + LocalStorageService.API_PREFIX.length());
    }

}
