package com.khutircraftubackend.search;

import com.khutircraftubackend.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {
	
	private final ProductSearchService productSearchService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<ProductResponse> search(
			@RequestParam String query) {
		
		return productSearchService.searchProducts(query);
	}
	
	@GetMapping("/suggestions")
	@ResponseStatus(HttpStatus.OK)
	public Collection<String> getSuggestions(
			@RequestParam String query) {
		
		return productSearchService.getSuggestions(query);
	}
}
