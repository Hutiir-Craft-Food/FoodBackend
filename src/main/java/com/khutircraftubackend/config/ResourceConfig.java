package com.khutircraftubackend.config;

import com.cloudinary.Cloudinary;
import com.khutircraftubackend.product.image.CloudinaryServiceImpl;
import com.khutircraftubackend.product.image.LocalStorageServiceImpl;
import com.khutircraftubackend.product.image.ResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class ResourceConfig {
	
	@Value("${storage.local.base-path:uploads/}")
	private String basePath;
	
	@Bean
	@Profile("!local")
	public ResourceService cloudinaryService(Cloudinary cloudinary) {
		
		return new CloudinaryServiceImpl(cloudinary);
	}
	
	@Bean
	@Primary
	@Profile("local")
	public ResourceService localStorageServices() {
		
		return new LocalStorageServiceImpl(basePath);
	}
	
}
