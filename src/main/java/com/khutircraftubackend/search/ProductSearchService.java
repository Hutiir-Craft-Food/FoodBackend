package com.khutircraftubackend.search;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final KeywordService keywordService;
	
	public Collection<ProductResponse> searchProducts(String query) {
		
		String postgresQuery = keywordService.processQuery(query);
		List<ProductEntity> result = productRepository.searchWithPriority(postgresQuery);
		
		return productMapper.toProductResponse(result);
	}
	
	public Collection<String> getSuggestions(String query) {
		
		return productRepository.findSuggestions(query);
	}
}
