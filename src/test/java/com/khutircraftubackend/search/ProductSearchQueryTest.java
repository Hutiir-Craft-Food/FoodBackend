package com.khutircraftubackend.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductSearchQueryTest {
    
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
}
