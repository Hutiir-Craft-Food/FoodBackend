package com.khutircraftubackend.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(LocalStorageController.API_PATH)
public class LocalStorageController {
    public static final String API_PATH = "/v1/resources";

    @Value("${storage.local.base-path:uploads}")
    private String basePath;

    @GetMapping(path = "/{fileName:.+}")
    public ResponseEntity<Resource> getResource(@PathVariable String fileName) {
        Path uploadPath = Paths.get(basePath);
        Path filePath = uploadPath.resolve(fileName).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.exists()) {
                return ResponseEntity.ok()
                        // TODO:
                        //  dynamically define the MediaType, refer to all available types below:
                        //  https://developer.mozilla.org/en-US/docs/Web/HTTP/MIME_types/Common_types
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
