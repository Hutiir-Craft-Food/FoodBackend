package com.khutircraftubackend.product.search;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.search.exception.GeneralSearchException;
import com.khutircraftubackend.product.search.response.ProductSearchItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.SEARCH_SERVICE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    public Map<String, Object> searchProductsByQuery(String query, int offset, int limit) {
    
        String validQuery = SearchQueryUtil.clean(query);
        
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
            response.put("pageNumber", offset / limit + 1);
            response.put("limit", limit);
            
            return response;
            
        } catch (Exception e) {
            log.error("Critical search error", e);
            throw new GeneralSearchException(SEARCH_SERVICE_ERROR);
        }
    }
}
