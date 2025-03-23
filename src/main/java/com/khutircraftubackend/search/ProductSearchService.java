package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.ProductRepository;
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
    
    public List<ProductSearchResult> searchProductsByQuery(String query) {
        
        if (query == null || query.length() < 2) {
            log.warn("Blocked suspicious query: '{}'", query);
            return List.of();
        }
        
        try {
            return productRepository.searchProducts(query);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return productRepository.searchProductsWithLike(query);
        }
        
    }
    
    @Transactional
    public Set<String> updateKeywords(Long categoryId, Set<String> keywords) {
        
        CategoryEntity category = categoryService.findCategoryById(categoryId);
        
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("Keywords cannot be empty");
        }
        
        Set<String> keywordsSet = new LinkedHashSet<>();
        
        if (category.getKeywords() != null && !category.getKeywords().isBlank()) {
            keywordsSet.addAll(Arrays.stream(category.getKeywords()
                            .split("\\s*,\\s*"))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toSet()));
        }
        
        keywordsSet.addAll(keywords.stream()
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase().trim())
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        
        String updatedKeywords = String.join(",", keywordsSet);
        
        category.setKeywords(updatedKeywords);
        
        categoryRepository.save(category);
        
        return keywordsSet;
    }
}
