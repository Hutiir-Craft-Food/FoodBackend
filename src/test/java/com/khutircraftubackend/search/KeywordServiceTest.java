package com.khutircraftubackend.search;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KeywordServiceTest {
	
	private final KeywordService keywordService = new KeywordService();
	
	@Test
	void generateKeywords() {

		String name = "Fanta mandarin";
		String category = "drinks";
		
		Set<String> result = KeywordService.generateKeywords(category, name);
		
		assertNotNull(result);
		assertTrue(result.contains("fanta"));
		assertTrue(result.contains("mandarin"));
		assertTrue(result.contains("drinks"));
		
	}
	
	@Test
	void processQuery() {
		
		String query = "fanta orange для drink";
		
		String result = keywordService.processQuery(query);
		
		assertNotNull(result);
		assertTrue(result.contains("fanta"));
		assertTrue(result.contains("orange"));
		assertTrue(result.contains("для"));
		assertTrue(result.contains("drink"));
		
		assertEquals("fanta & orange & для & drink:*", result);
	}
	
	@Test
	void generateKeywordsWithNullValues() {

		String name = null;
		String category = "Test category";
		
		Set<String> result = KeywordService.generateKeywords(name, category);
		
		assertNotNull(result);
		assertTrue(result.contains("test"));
		assertTrue(result.contains("category"));
	}
}