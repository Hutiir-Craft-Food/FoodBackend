package com.khutircraftubackend.storage.config;

import com.cloudinary.Cloudinary;
import com.khutircraftubackend.storage.CloudinaryServiceImpl;
import com.khutircraftubackend.storage.StorageServiceImpl;
import com.khutircraftubackend.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class StorageConfig {
	
	@Bean
	@Profile("!local")
	public StorageService cloudinaryService(Cloudinary cloudinary) {
		
		return new CloudinaryServiceImpl(cloudinary);
	}
	
	@Bean
	@Primary
	@Profile("local")
	public StorageService localStorageService(
			@Value("${storage.local.base-path:./uploads}") String basePath) {
		log.info("Base path: " + basePath);
		return new StorageServiceImpl(basePath);
	}
	
}
