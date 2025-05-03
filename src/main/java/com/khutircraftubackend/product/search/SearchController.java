package com.khutircraftubackend.product.search;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final ProductSearchService productSearchService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getProducts(
            @NotBlank(message = EMPTY_QUERY_ERROR)
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int limit) {
        
        return productSearchService.searchProductsByQuery(query, pageNumber, limit);
    }
}
