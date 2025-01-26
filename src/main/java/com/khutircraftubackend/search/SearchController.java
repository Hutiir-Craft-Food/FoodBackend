package com.khutircraftubackend.search;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {
	private final ProductSearchService productSearchService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Object> search(
			@RequestParam String query) {
		
		return productSearchService.searchProducts(query);
	}
	
	@GetMapping("/suggestions")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, Object> getSuggestions(
			@RequestParam String query) {
		
		return productSearchService.getSuggestions(query);
	}
}
