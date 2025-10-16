package com.khutircraftubackend.storage.config;

import com.cloudinary.Cloudinary;
import com.khutircraftubackend.storage.CloudinaryService;
import com.khutircraftubackend.storage.LocalStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Import({CloudinaryConfig.class})
public class StorageConfig {
    
    @Bean
    @Profile("!local")
    public CloudinaryService cloudinaryService(Cloudinary cloudinary) {

        return new CloudinaryService(cloudinary);
    }

    @Bean
    @Profile("local")
    public LocalStorageService localStorageService(@Value("${storage.local.base-path}") String basePath) {
        
        return new LocalStorageService(basePath);
    }
    
}
