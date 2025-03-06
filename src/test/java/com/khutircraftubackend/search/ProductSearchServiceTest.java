package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
 class ProductSearchServiceTest {
    @Mock
    private CategoryService categoryService;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private ProductSearchService productSearchService;
    
    private CategoryEntity category;
    
    @BeforeEach
    void setup() {
        category = new CategoryEntity();
        category.setId(8L);
        category.setKeywords("вода, напій");
        
        when(categoryService.findCategoryById(8L)).thenReturn(category);
    }
    
    @Test
    void shouldAddNewKeywordsWithoutDuplicates() {
        Long categoryId = 8L;
        Set<String> newKeywords = Set.of("сік", "вода", "чай");
        
        Set<String> result = productSearchService.updateKeywords(categoryId, newKeywords);
        
        assertThat(result).containsExactlyInAnyOrder("вода", "напій", "сік", "чай");
        verify(categoryRepository).save(category);
    }
    
    @Test
    void shouldThrowExceptionWhenKeywordsAreEmpty() {
        Long categoryId = 8L;
        Set<String> newKeywords = Set.of();
        
        assertThatThrownBy(() -> productSearchService.updateKeywords(categoryId, newKeywords))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Keywords cannot be empty");
    }
    
    @Test
    void shouldHandleEmptyDatabaseKeywords() {
        category.setKeywords(null);
        Long categoryId = 8L;
        Set<String> newKeywords = new LinkedHashSet<>(List.of("фрукти", "ягоди"));
        
        Set<String> result = productSearchService.updateKeywords(categoryId, newKeywords);
        
        assertThat(result).containsExactlyInAnyOrder("фрукти", "ягоди");
        assertThat(category.getKeywords()).isEqualTo("фрукти,ягоди");
        verify(categoryRepository).save(category);
    }
    
    @Test
    void shouldIgnoreDuplicateCommasAndSpaces() {
        Long categoryId = 8L;
        Set<String> newKeywords = new LinkedHashSet<>(List.of("  ", " лимон  ", "апельсин", "", "  банан  ", "   "));
        
        Set<String> result = productSearchService.updateKeywords(categoryId, newKeywords);
        
        assertThat(result).containsExactlyInAnyOrder("вода", "напій", "лимон", "апельсин", "банан");
        assertThat(category.getKeywords()).isEqualTo("вода,напій,лимон,апельсин,банан");
    }
    
    @Test
    void shouldThrowExceptionWhenKeywordsAreNull() {
        Long categoryId = 8L;
        
        assertThatThrownBy(() -> productSearchService.updateKeywords(categoryId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Keywords cannot be empty");
    }
}
