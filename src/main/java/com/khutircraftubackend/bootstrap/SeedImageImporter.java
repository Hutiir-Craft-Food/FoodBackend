package com.khutircraftubackend.bootstrap;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.image.ProductImageService;
import com.khutircraftubackend.product.image.request.ProductImageUploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedImageImporter {

	private final ResourceLoader resourceLoader;
	private final ProductRepository productRepository;
	private final ProductImageService productImageService;

	public void importImages() {

		for (Map.Entry<String, String> entry : SeedProductImageRegistry.IMAGES.entrySet()) {
			String productName = entry.getKey();
			String fileName = entry.getValue();

			productRepository.findByName(productName).ifPresentOrElse(
					product -> seedImage(product, fileName),
					() -> log.warn("Product '{}' not found, skipping", productName)
			);
		}
	}

	private ProductImageUploadRequest buildRequest() {

		return new ProductImageUploadRequest(
				List.of(new ProductImageUploadRequest.UploadImageInfo(1))
		);
	}


	private void seedImage(ProductEntity product, String fileName) {

		Resource resource = resourceLoader.getResource("classpath:bootstrap/image/" + fileName);

		if (!resource.exists()) {
			log.warn("Seed image skipped: file '{}' not found", fileName);
			return;
		}

		try {
			MultipartFile file = new ResourceMultipartFile(resource, fileName);
			ProductImageUploadRequest request = buildRequest();
			productImageService.uploadImages(product.getId(), request, List.of(file));
		} catch (Exception e) {
			log.error("Failed to seed image '{}' for product '{}'", fileName, product.getName(), e);
		}
	}

}
