package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.product.ProductEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class KeywordService {
	
	public static Set<String> generateKeywords(ProductEntity product, CategoryEntity category) {
		List<String> sources = Arrays.asList(product.getName(), category.getName());
		
		return sources.stream()
				.filter(Objects::nonNull)
				.flatMap(text -> Arrays.stream(text.split("\\s+")))
				.map(String::toLowerCase)
				.filter(word -> word.length() > 2)
				.collect(Collectors.toSet());
	}
	
	public static String processQuery(String query) {
		
		if (query == null || query.isBlank()) {
			return "";
		}
		return Arrays.stream(query.split("\\s+"))
				.map(String::toLowerCase)
				.filter(word -> word.length() > 1)
				.collect(Collectors.joining(" & ")) + ":*";
	}
	
}
