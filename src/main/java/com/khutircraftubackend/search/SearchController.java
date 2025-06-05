package com.khutircraftubackend.search;

import com.khutircraftubackend.search.response.ProductSearchView;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.khutircraftubackend.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;

@RestController
@RequestMapping("/v1/products/search")
@RequiredArgsConstructor
public class SearchController {
	private final SearchService searchService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ProductSearchView> getProducts (
			@NotBlank(message = EMPTY_QUERY_ERROR)
			@RequestParam String query
	) {
			return searchService.searchProductsByQuery(query);
	}
}
