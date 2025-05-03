package com.khutircraftubackend.product.search;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.search.exception.GeneralSearchException;
import com.khutircraftubackend.product.search.exception.SearchResponseMessage;
import com.khutircraftubackend.product.search.response.ProductSearchItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    public Map<String, Object> searchProductsByQuery(String query, int pageNumber, int limit) {
    
        String validQuery = SearchQueryUtil.clean(query);
        
        int offset = pageNumber * limit;
        
        try {
            List<ProductSearchItemResponse> products = productRepository
                    .searchProducts(validQuery, limit, offset)
                    .stream()
                    .map(productMapper::toSearchItemResponse)
                    .toList();
            
            long total = productRepository.countProducts(validQuery);
    
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("total", total);
            response.put("offset", offset);
            response.put("limit", limit);
            
            return response;
            
        } catch (Exception e) {
            log.error("Critical search error", e);
            throw new GeneralSearchException(SearchResponseMessage.SEARCH_SERVICE_ERROR);
        }
    }
}
