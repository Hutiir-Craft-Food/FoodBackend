package com.khutircraftubackend.product.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SearchQueryUtilTest {
    
    @Mock
    CategoryService categoryService;
    
    @BeforeEach
    void setUp() {
        CategoryEntity category = new CategoryEntity();
        category.setId(8L);
        
        lenient().when(categoryService.findCategoryById(8L)).thenReturn(category);
        
    }
    
    @DisplayName("Should return cleaned query for various inputs")
    @ParameterizedTest
    @CsvSource({"'Hello <b>bold</b> world'," +
            " 'Hello bold world'",
            "'<script>alert(''Foo'')</script>Bar', 'Bar'",
            "'&lt;div&gt;Hello&lt;/div&gt;', 'Hello'",
            "'valid_query-123', 'valid_query-123'"})
    void shouldReturnCleanedQuery(String query, String expected) {
        
        // Act
        String cleaned = SearchQueryUtil.clean(query);
        
        // Assert
        assertEquals(expected, cleaned);
    }
    
    @DisplayName("Should normalize spaces in queries")
    @ParameterizedTest
    @CsvSource({"'  multiple   spaces  \nwith\ttabs ', 'multiple spaces with tabs'",
            "'   spaced    out   words   ', 'spaced out words'"})
    void shouldNormalizeSpaces(String query, String expected) {
        
        // Act
        String cleaned = SearchQueryUtil.clean(query);
        
        // Assert
        assertEquals(expected, cleaned);
    }
    
    @DisplayName("Should throw exception when cleaned query is blank")
    @Test
    void shouldThrowWhenCleanedQueryIsBlank() {
        
        // Arrange
        String query = "<script></script>";
        
        // Act
        InvalidSearchQueryException exception = assertThrows(InvalidSearchQueryException.class, () -> SearchQueryUtil.clean(query));
        
        // Assert
        assertEquals(EMPTY_QUERY_ERROR, exception.getMessage());
    }
}
