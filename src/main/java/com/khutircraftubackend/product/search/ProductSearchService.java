package com.khutircraftubackend.product.search;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.search.exception.GeneralSearchException;
import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import com.khutircraftubackend.product.search.response.ProductSearchItemResponse;
import com.khutircraftubackend.product.search.response.ProductSearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;
import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.SEARCH_SERVICE_ERROR;

/**
 * The type Product search service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    public Map<String, Object> searchProductsByQuery(String query, int offset, int limit) {
    
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidSearchQueryException(EMPTY_QUERY_ERROR);
        }
    
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);
    
        String validQuery = SearchQueryUtil.clean(query);
        
        try {
            Page<ProductSearchResult> page = productRepository.searchProducts(validQuery, pageable);
            Collection<ProductSearchItemResponse> products = productMapper.toSearchResponse(page.getContent());
            
            long total = page.getTotalElements();
    
            return Map.of(
                    "products", products,
                    "total", total,
                    "offset", offset,
                    "limit", limit
            );
        } catch (Exception e) {
            log.error("Critical search error", e);
            throw new GeneralSearchException(SEARCH_SERVICE_ERROR);
        }
    }
}
