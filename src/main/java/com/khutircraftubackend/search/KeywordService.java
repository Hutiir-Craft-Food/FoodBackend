package com.khutircraftubackend.search;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KeywordService {
	
	public static Set<String> generateKeywords(String name, String category) {
		List<String> sources = Arrays.asList(name, category);
		
		return sources.stream()
				.filter(Objects::nonNull)
				.flatMap(text -> Arrays.stream(text.split("\\s+")))
				.map(String::toLowerCase)
				.filter(word -> word.length() > 2)
				.collect(Collectors.toSet());
	}
	
	//ALTER TABLE FOR INDEX
	public String processQuery(String query) {
		
		return Arrays.stream(query.split("\\s+"))
				.map(String::toLowerCase)
				.filter(word -> word.length() > 1)
				.collect(Collectors.joining(" & ")) + ":*";//формат to_tsquery
	}
	
}
