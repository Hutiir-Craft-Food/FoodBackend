package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ProductSearchQueryTest {
    @InjectMocks
    ProductSearchService productSearchService;
    @Mock
    CategoryService categoryService;
    
    @BeforeEach
    void setUp() {
        CategoryEntity category = new CategoryEntity();
        category.setId(8L);
        
        lenient().when(categoryService.findCategoryById(8L)).thenReturn(category);
        
    }
    
    @DisplayName("Should return valid query when input is tags")
    @Test
    void shouldRemoveHtmlTagsEverywhere() {
        ProductSearchQuery query = new ProductSearchQuery("Hello <b>bold</b> world");
        assertEquals("Hello bold world", query.query());
    }
    
    @DisplayName("Should return valid query when input is query with symbol")
    @Test
    void shouldReturnHtmlSymbolRemoval() {
        ProductSearchQuery query = new ProductSearchQuery("ков&#91");
        assertEquals("ков[", query.query());
    }
    
    @DisplayName("Should return valid query when input is characters")
    @Test
    void shouldReturnHtmlEscapedCharacters() {
        ProductSearchQuery query = new ProductSearchQuery("&lt;div&gt;Hello&lt;/div&gt;");
        assertEquals("Hello", query.query());
    }
    
    @DisplayName("Should return valid query when input is spaces")
    @Test
    void shouldRemoveExcessiveSpaces() {
        ProductSearchQuery query = new ProductSearchQuery("  multiple   spaces  \nwith\ttabs ");
        assertEquals("multiple spaces with tabs", query.query());
    }
    
    @DisplayName("Should return valid query when input is valid query")
    @Test
    void shouldValidQuery() {
        ProductSearchQuery query = new ProductSearchQuery("valid_query-123");
        assertEquals("valid_query-123", query.query());
    }
    
    @DisplayName("Should return valid query when keywords is empty")
    @Test
    void shouldThrowExceptionWhenKeywordsAreEmpty() {
        Long categoryId = 8L;
        Set<String> newKeywords = Set.of();
        
        assertThatThrownBy(() -> productSearchService.updateKeywords(categoryId, newKeywords))
                .isInstanceOf(InvalidSearchQueryException.class)
                .hasMessage(SearchResponseMessage.EMPTY_KEYWORDS_ERROR);
    }
    
}
