package com.khutircraftubackend.search;

import com.khutircraftubackend.product.ProductEntity;
import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.response.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {
	
	@Mock
	ProductRepository productRepository;
	
	@Mock
	ProductMapper productMapper;
	
	@InjectMocks
	ProductSearchService productSearchService;
	
	@Test
	void searchProducts() {
		
		String query = "example query";

		ProductEntity productEntity1 = ProductEntity.builder()
				.id(1L)
                .name("Product1")
                .description("Desc1")
                .available(true)
                .build();
		
		ProductEntity productEntity2 = ProductEntity.builder()
				.id(2L)
                .name("Product2")
                .description("Desc2")
                .available(true)
                .build();
		
		List<ProductEntity> productEntities = List.of(productEntity1, productEntity2);
		
		ProductResponse productResponses1 = ProductResponse.builder()
				.id(1L)
                .name("Product1")
                .description("Desc1")
                .available(true)
                .build();
		
		ProductResponse productResponses2 = ProductResponse.builder()
				.id(2L)
                .name("Product2")
                .description("Desc2")
                .available(true)
                .build();
		
		List<ProductResponse> productResponses = List.of(productResponses1, productResponses2);
		
		when(productRepository.searchWithPriority("example & query:*")).thenReturn(productEntities);
		when(productMapper.toProductResponse(productEntities)).thenReturn(productResponses);
		
		Map<String, Object> result = productSearchService.searchProducts(query);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(query, result.get("query"));
		assertEquals(productResponses, result.get("products"));
		
		verify(productRepository).searchWithPriority("example & query:*");
		verify(productMapper).toProductResponse(productEntities);
	}
	
	@Test
	void searchProductsNoResults() {

		String query = "nonexistent query";
		
		when(productRepository.searchWithPriority("nonexistent & query:*")).thenReturn(List.of());
		when(productMapper.toProductResponse(List.of())).thenReturn(List.of());
		
		Map<String, Object> result = productSearchService.searchProducts(query);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(query, result.get("query"));
		assertTrue(((Collection<?>) result.get("products")).isEmpty());
		
		verify(productRepository).searchWithPriority("nonexistent & query:*");
		verify(productMapper).toProductResponse(List.of());
	}
	
	@Test
	void searchProductsEmptyQuery() {
		String query = "";
		
		when(productRepository.searchWithPriority(anyString())).thenReturn(Collections.emptyList());
		when(productMapper.toProductResponse(anyList())).thenReturn(Collections.emptyList());
		
		Map<String, Object> result = productSearchService.searchProducts(query);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(query, result.get("query"));
		assertTrue(((Collection<?>) result.get("products")).isEmpty());
		
		verify(productRepository).searchWithPriority("");
		verify(productMapper).toProductResponse(Collections.emptyList());
	}
	
	@Test
	void searchProductsNullQuery() {
		String query = null;
		
		when(productRepository.searchWithPriority("")).thenReturn(Collections.emptyList());
		when(productMapper.toProductResponse(anyList())).thenReturn(Collections.emptyList());
		
		Map<String, Object> result = productSearchService.searchProducts(query);
		
		assertNotNull(result);
		assertTrue(result.containsKey("query"));
		assertEquals("", result.get("query"));
		assertTrue(((Collection<?>) result.get("products")).isEmpty());
		
		verify(productRepository).searchWithPriority("");
		verify(productMapper).toProductResponse(Collections.emptyList());
	}
	
	@Test
	void getSuggestions() {
		
		String query = "Ковбаса";
		
        List<String> mockSuggestions = List.of("Ковбаса", "вʼялена", "варена");
		
		when(productRepository.findSuggestions(query)).thenReturn(mockSuggestions);
		
		Map<String, Object> suggestions = productSearchService.getSuggestions(query);
		
		assertNotNull(suggestions);
		assertEquals(2, suggestions.size());
		assertEquals(query, suggestions.get("query"));
		assertEquals(mockSuggestions, suggestions.get("suggestions"));
		
		verify(productRepository, times(1)).findSuggestions(query);
	}
	
	@Test
	void getSuggestionsEmptyQuery() {
		String query = "";
		
		when(productRepository.findSuggestions(query)).thenReturn(Collections.emptyList());
		
		Map<String, Object> suggestions = productSearchService.getSuggestions(query);
		
		assertNotNull(suggestions);
		assertEquals(2, suggestions.size());
		assertEquals(query, suggestions.get("query"));
		assertTrue(((Collection<?>) suggestions.get("suggestions")).isEmpty());
		
		verify(productRepository, times(1)).findSuggestions(query);
	}
	
	@Test
	void getSuggestionsNullQuery() {
		String query = null;
		
		when(productRepository.findSuggestions("")).thenReturn(Collections.emptyList());
		
		Map<String, Object> suggestions = productSearchService.getSuggestions(query);
		
		assertNotNull(suggestions);
		assertTrue(suggestions.containsKey("query"));
		assertEquals("", suggestions.get("query"));
		assertTrue(((Collection<?>) suggestions.get("suggestions")).isEmpty());
		
		verify(productRepository, times(1)).findSuggestions("");
	}
	
	@Test
	void processQueryHandlesNullAndEmpty() {

		String nullQuery = null;
		String processedNull = KeywordService.processQuery(nullQuery);
		assertNotNull(processedNull);
		assertEquals("", processedNull);
		
		String emptyQuery = "";
		String processedEmpty = KeywordService.processQuery(emptyQuery);
		assertNotNull(processedEmpty);
		assertEquals("", processedEmpty);
		
		String validQuery = "Example Query";
		String processedValid = KeywordService.processQuery(validQuery);
		assertNotNull(processedValid);
		assertEquals("example & query:*", processedValid);
	}
}