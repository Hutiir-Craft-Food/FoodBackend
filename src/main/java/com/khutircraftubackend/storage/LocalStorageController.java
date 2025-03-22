package com.khutircraftubackend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;

import static com.khutircraftubackend.storage.LocalStorageController.API_PATH;

@RestController
@Profile("local")
@RequestMapping(API_PATH)
@RequiredArgsConstructor
public class LocalStorageController {
    public static final String API_PATH = "/v1/resources";
    public final LocalStorageService storageService;
    
    @GetMapping("/{fileName:.+}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getResource(@PathVariable String fileName) throws IOException {
        
        Resource resource = storageService.getResource(fileName);
        
        String contentType = Files.probeContentType(resource.getFile().toPath());
        
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
}
