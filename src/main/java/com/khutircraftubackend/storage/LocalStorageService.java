package com.khutircraftubackend.storage;

import com.khutircraftubackend.exception.httpstatus.NotFoundException;
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

    @Override
    public String upload(byte[] fileBytes, String originalFileName) throws IOException {
        Path uploadPath = Paths.get(basePath);
        Files.createDirectories(uploadPath);
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(new ByteArrayInputStream(fileBytes), filePath);

        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();
        String relativeUriStr = LocalStorageController.API_PATH + uploadPath
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
    public String upload(MultipartFile multipartFile) throws IOException {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidFileFormatException(StorageResponseMessage.INVALID_FILE);
        }

        String originalFileName = multipartFile.getOriginalFilename();
        return upload(multipartFile.getBytes(), originalFileName);
    }

    public Resource getResource(String fileName) {

        Path filePath = Paths.get(basePath).resolve(fileName).normalize();

        if (Files.notExists(filePath)) {
            throw new NotFoundException(String.format(StorageResponseMessage.FILE_NOT_FOUND, filePath));
        }

        return new FileSystemResource(filePath);
    }

    @Override
    public void deleteByUrl(String fileUrl) throws IOException {

        if (!fileUrl.contains(LocalStorageController.API_PATH)) {
            throw new InvalidArgumentException(String.format(StorageResponseMessage.INVALID_ARGUMENT, fileUrl));
        }

        String filePathStr = fileUrl.split(LocalStorageController.API_PATH + "/")[1]; //TODO magic number

        Path filePath = Paths.get(basePath).resolve(filePathStr);

        if (Files.notExists(filePath)) {
            throw new NotFoundException(String.format(StorageResponseMessage.FILE_NOT_FOUND, filePathStr));
        }
        Files.delete(filePath);
    }

}
