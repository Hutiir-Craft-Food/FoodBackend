package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.search.exception.GeneralSearchException;
import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    
    public List<ProductSearchResult> searchProductsByQuery(ProductSearchQuery productSearchQuery) {
        
        String query = productSearchQuery
                .query()
                .toLowerCase();
        
        if(query.isBlank()) {
            throw new InvalidSearchQueryException(SearchResponseMessage.EMPTY_QUERY_ERROR);
        }
        
        try {
            return productRepository.searchProducts(query);
        } catch (Exception e) {
            log.error("Critical search error: {}", e.getMessage());
            throw new GeneralSearchException(SearchResponseMessage.SEARCH_SERVICE_ERROR);
        }
    }
    
    @Transactional
    public Set<String> updateKeywords(Long categoryId, Set<String> keywords) {
        
        CategoryEntity category = categoryService.findCategoryById(categoryId);
        
        if (keywords == null || keywords.isEmpty()) {
            throw new InvalidSearchQueryException(SearchResponseMessage.EMPTY_KEYWORDS_ERROR);
        }
    
        Set<String> validKeywords = keywords.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> {
                    if (!s.matches("^[\\p{L}\\d\\s_'ʼ.,\\-]+$")) {
                        throw new InvalidSearchQueryException(SearchResponseMessage.NOT_VALID_SYMBOL);
                    }
                    return !s.isBlank();
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
        
        Set<String> keywordsSet = new LinkedHashSet<>();
        
        if (category.getKeywords() != null && !category.getKeywords().isBlank()) {
            keywordsSet.addAll(Arrays.stream(category.getKeywords()
                            .split("\\s*,\\s*"))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toSet()));
        }
        
        keywordsSet.addAll(validKeywords);
        
        category.setKeywords(String.join(",", keywordsSet));
        
        categoryRepository.save(category);
        
        return keywordsSet;
    }
}
