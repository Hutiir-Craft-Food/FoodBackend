package com.khutircraftubackend.search;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	
	/**
	 * Пошук продуктів за ключовим словом із пріоритетом.
	 *
	 * @param query Ключове слово для пошуку.
	 * @return Мапа з ключовим словом та результатами пошуку.
	 */
	public Map<String, Object> searchProducts(String query) {
		
		String postgresQuery = query == null ? "" : KeywordService.processQuery(query);
		List<ProductEntity> result = productRepository.searchWithPriority(postgresQuery);
		Collection<ProductResponse> products = productMapper.toProductResponse(result);
		
		return Map.of(
				"query", query == null ? "" : query,
				"products", products
		);
	}
	
	/**
	 * Отримання підказок для пошуку.
	 *
	 * @param query Ключове слово для пошуку.
	 * @return Мапа з ключовим словом та списком підказок.
	 */
	public Map<String, Object> getSuggestions(String query) {
		String processedQuery = (query == null || query.isBlank()) ? "" : query;
		
		return Map.of(
				"query", processedQuery,
				"suggestions", productRepository.findSuggestions(processedQuery)
		);
	}}
