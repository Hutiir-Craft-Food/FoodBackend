package com.khutircraftubackend.storage.config;

import com.cloudinary.Cloudinary;
import com.khutircraftubackend.storage.CloudinaryServiceImpl;
import com.khutircraftubackend.storage.LocalStorageServiceImpl;
import com.khutircraftubackend.storage.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@Import(CloudinaryConfig.class)
public class StorageConfig {
	
	@Value("${storage.local.base-path:uploads}")
	private String basePath;


	@Bean
	@Profile("!local")
	public StorageService cloudinaryService(Cloudinary cloudinary) {
		return new CloudinaryServiceImpl(cloudinary);
	}
	
	@Bean
	@Primary
	@Profile("local")
	public StorageService localStorageService() {
		return new LocalStorageServiceImpl(basePath);
	}

}
