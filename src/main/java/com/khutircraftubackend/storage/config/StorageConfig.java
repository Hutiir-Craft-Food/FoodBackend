package com.khutircraftubackend.storage.config;

import com.cloudinary.Cloudinary;
import com.khutircraftubackend.storage.CloudinaryService;
import com.khutircraftubackend.storage.LocalStorageController;
import com.khutircraftubackend.storage.LocalStorageService;
import com.khutircraftubackend.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
	public StorageService cloudinaryService(Cloudinary cloudinary) {

		return new CloudinaryService(cloudinary);
	}
	
	@Bean
	@Profile("local")
	public StorageService localStorageService(@Value("${storage.local.base-path}") String basePath,
											  @Value("${storage.local.api-path}") String apiPath) {
		log.info("Local Storage base-path: {}", basePath);
		log.info("Local Storage api-path: {}", apiPath);
		return new LocalStorageService(basePath, apiPath);
	}
	
	@Bean
	@Profile("local")
	public LocalStorageController localStorageController(
			@Qualifier("localStorageService") StorageService storageService,
			@Value("${storage.local.api-path}") String apiPath) {
		return new LocalStorageController((LocalStorageService) storageService, apiPath);
	}
}
