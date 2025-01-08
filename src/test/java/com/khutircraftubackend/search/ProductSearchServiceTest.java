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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {
	
	@Mock
	ProductRepository productRepository;
	
	@Mock
	ProductMapper productMapper;
	
	@Mock
	KeywordService keywordService;
	
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
				.thumbnailImageUrl(null)
				.imageUrl(null)
                .build();
		
		ProductEntity productEntity2 = ProductEntity.builder()
				.id(2L)
                .name("Product2")
                .description("Desc2")
                .available(true)
                .thumbnailImageUrl(null)
                .imageUrl(null)
                .build();
		
		List<ProductEntity> productEntities = List.of(productEntity1, productEntity2);
		
		ProductResponse productResponses1 = ProductResponse.builder()
				.id(1L)
                .name("Product1")
                .description("Desc1")
                .available(true)
                .thumbnailImageUrl(null)
                .imageUrl(null)
                .build();
		
		ProductResponse productResponses2 = ProductResponse.builder()
				.id(2L)
                .name("Product2")
                .description("Desc2")
                .available(true)
                .thumbnailImageUrl(null)
                .imageUrl(null)
                .build();
		
		List<ProductResponse> productResponses = List.of(productResponses1, productResponses2);
		
		when(keywordService.processQuery(query)).thenReturn("example & query:*");
		when(productRepository.searchWithPriority("example & query:*")).thenReturn(productEntities);
		when(productMapper.toProductResponse(productEntities)).thenReturn(productResponses);
		
		Collection<ProductResponse> result = productSearchService.searchProducts(query);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.containsAll(productResponses));
		
		verify(keywordService).processQuery(query);
		verify(productRepository).searchWithPriority("example & query:*");
		verify(productMapper).toProductResponse(anyList());
	}
	
	@Test
	void searchProductsNoResults() {

		String query = "nonexistent query";
		
		when(keywordService.processQuery(query)).thenReturn("nonexistent & query:*");
		when(productRepository.searchWithPriority("nonexistent & query:*")).thenReturn(List.of());
		when(productMapper.toProductResponse(List.of())).thenReturn(List.of());
		
		Collection<ProductResponse> result = productSearchService.searchProducts(query);
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
		
		verify(keywordService).processQuery(query);
		verify(productRepository).searchWithPriority("nonexistent & query:*");
		verify(productMapper).toProductResponse(List.of());
	}
	
	@Test
	void getSuggestions() {
		
		String query = "Ковбаса";
		
        List<String> mockSuggestions = List.of("Ковбаса", "вʼялена", "варена");
		
		when(productRepository.findSuggestions(query)).thenReturn(mockSuggestions);
		
		Collection<String> suggestions = productSearchService.getSuggestions(query);
		
		assertEquals(mockSuggestions, suggestions);
		
		verify(productRepository, times(1)).findSuggestions(query);
	}
}