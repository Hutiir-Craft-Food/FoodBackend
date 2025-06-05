package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SearchQueryUtilTest {

    @Mock
    CategoryService categoryService;

    @InjectMocks
    SearchService searchService;

    @BeforeEach
    void setUp() {
        CategoryEntity category = new CategoryEntity();
        category.setId(8L);

        lenient().when(categoryService.findCategoryById(8L)).thenReturn(category);
    }

    @DisplayName("Should return valid query when input is tags")
    @Test
    void shouldRemoveHtmlTagsEverywhere() {
        String query = "Hello <b>bold</b> world";
        assertEquals("Hello bold world", SearchQueryUtil.clean(query));
    }
    
    @DisplayName("Should return valid query when input is characters")
    @Test
    void shouldReturnHtmlEscapedCharacters() {
        String query = "&lt;div&gt;Hello&lt;/div&gt;";
        assertEquals("Hello", SearchQueryUtil.clean(query));
    }
    
    @DisplayName("Should return valid query when input is spaces")
    @Test
    void shouldRemoveExcessiveSpaces() {
        String query = ("  multiple   spaces  \nwith\ttabs ");
        assertEquals("multiple spaces with tabs", SearchQueryUtil.clean(query));
    }
    
    @DisplayName("Should return valid query when input is valid query")
    @Test
    void shouldValidQuery() {
        String query = "valid_query-123";
        assertEquals("valid_query-123", SearchQueryUtil.clean(query));
    }

    @Test
    void shouldClearQueryFromHtmlTags() {
        String query = "<script>alert('Foo')</script>Bar";
        assertEquals("Bar", SearchQueryUtil.clean(query));
    }
}
