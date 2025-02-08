package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.product.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {
	@Test
	void generateKeywords() {
		
		ProductEntity productEntity = new ProductEntity();
		productEntity.setName("Fanta mandarin");
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setName("drinks");
		categoryEntity.setParentCategory(new CategoryEntity());
		categoryEntity.getParentCategory().setName("beverages");
		
		Set<String> result = KeywordService.generateKeywords(productEntity, categoryEntity);
		
		assertNotNull(result);
		assertTrue(result.contains("fanta"));
		assertTrue(result.contains("mandarin"));
		assertTrue(result.contains("drinks"));
		assertTrue(result.contains("beverages"));
		assertFalse(result.contains("dr"));
		
	}
	
	@Test
	void processQuery() {
		
		String query = "fanta orange для drink";
		
		String result = KeywordService.processQuery(query);
		
		assertNotNull(result);
		assertTrue(result.contains("fanta"));
		assertTrue(result.contains("orange"));
		assertTrue(result.contains("для"));
		assertTrue(result.contains("drink"));
		assertEquals("fanta orange для drink", result);
	}
	
	@Test
	void generateKeywordsWithNullValues() {
		ProductEntity productEntity = new ProductEntity();
		productEntity.setName(null);
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setName("Test category");
		categoryEntity.setParentCategory(null);
		
		Set<String> result = KeywordService.generateKeywords(productEntity, categoryEntity);
		
		assertNotNull(result);
		assertTrue(result.contains("test"));
		assertTrue(result.contains("category"));
	}
}