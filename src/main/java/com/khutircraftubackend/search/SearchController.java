package com.khutircraftubackend.search;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {
	private final ProductSearchService productSearchService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ProductSearchResult> getProducts(
			@Valid @ModelAttribute ProductSearchQuery query) {
		
			return productSearchService.searchProductsByQuery(query);
	}
}
