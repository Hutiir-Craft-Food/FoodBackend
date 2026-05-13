package com.khutircraftubackend.bootstrap;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@ConditionalOnProperty(prefix = "seed.images", name = "enabled", havingValue = "true")
public class SeedConfiguration {

	@Bean
	ApplicationRunner seedImageRunner(SeedImageImporter importer) {

		return args -> importer.importImages();
	}
}
