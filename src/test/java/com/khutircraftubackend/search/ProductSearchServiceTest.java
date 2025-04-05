package com.khutircraftubackend.search;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.CategoryService;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.search.exception.GeneralSearchException;
import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.HtmlUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
class ProductSearchServiceTest {
    @Mock
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductSearchService productSearchService;
    private CategoryEntity category;
    
    @BeforeEach
    void setUp() {
        category = new CategoryEntity();
        category.setId(8L);
        category.setKeywords("вода, напій");
        
        lenient().when(categoryService.findCategoryById(8L)).thenReturn(category);
        
    }
    
    @Test
    void shouldClearQueryFromHtmlUtils() {
        String input = "<script>alert('XSS')</script>XSS";
        
        ProductSearchQuery query = new ProductSearchQuery(input);
        
        assertThat(query.query()).isEqualTo("XSS");
    }
    
    @Test
    void shouldCleanHtmlUsingJsoupOnly() {
        String input = "<script>alert('Foo')</script>Bat";
        
        String cleaned = Jsoup.clean(HtmlUtils.htmlUnescape("<script>alert('Foo')</script>Bar"), Safelist.none());
        
        assertThat(cleaned).isEqualTo("Bar");
    }
    
    @Nested()
    @DisplayName("Keyword Operations")
    class Keywords {
        @Test
        void shouldAddNewKeywordsWithoutDuplicates() {
            Long categoryId = 8L;
            Set<String> newKeywords = Set.of("сік", "вода", "чай");
            
            Set<String> result = productSearchService.updateKeywords(categoryId, newKeywords);
            
            assertThat(result).containsExactlyInAnyOrder("вода", "напій", "сік", "чай");
            verify(categoryRepository).save(category);
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
            Set<String> newKeywords = new LinkedHashSet<>(List.of(" лимон  ", "апельсин", "  банан  "));
            
            Set<String> result = productSearchService.updateKeywords(categoryId, newKeywords);
            
            assertThat(result).containsExactlyInAnyOrder("вода", "напій", "лимон", "апельсин", "банан");
            assertThat(category.getKeywords()).isEqualTo("вода,напій,лимон,апельсин,банан");
        }
        
        @Test
        void shouldThrowExceptionWhenKeywordsAreNull() {
            Long categoryId = 8L;
            
            assertThatThrownBy(() -> productSearchService.updateKeywords(categoryId, null))
                    .isInstanceOf(InvalidSearchQueryException.class)
                    .hasMessage(SearchResponseMessage.EMPTY_KEYWORDS_ERROR);
        }
    }
    
    @Nested
    @DisplayName("Search Product Operations")
    class SearchProducts {
        
        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should throw InvalidSearchQueryException for blank inputs")
        void shouldThrowInvalidSearchQueryExceptionForBlankInputs(String input) {
            ProductSearchQuery query = new ProductSearchQuery(input);
            InvalidSearchQueryException exception = assertThrows(
                    InvalidSearchQueryException.class,
                    () -> productSearchService.searchProductsByQuery(query)
            );
            assertEquals(SearchResponseMessage.EMPTY_QUERY_ERROR, exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Search Exception Handling")
    class SearchException {
        @Test
        void shouldThrowGeneralExceptionOnCriticalError() {
            String validQuery = "apple banana";
            ProductSearchQuery query = new ProductSearchQuery(validQuery);
            
            when(productRepository.searchProducts(validQuery)).thenThrow(new GeneralSearchException("Simulated critical error"));
            
            assertThatThrownBy(() -> productSearchService.searchProductsByQuery(query))
                    .isInstanceOf(GeneralSearchException.class)
                    .hasMessage(SearchResponseMessage.SEARCH_SERVICE_ERROR);
            
            verify(productRepository).searchProducts(validQuery);
        }
        
        @Test
        void shouldThrowGeneralExceptionOnUnexpectedError() {
            String validQuery = "apple banana";
            ProductSearchQuery query = new ProductSearchQuery(validQuery);
            
            when(productRepository.searchProducts(validQuery)).thenThrow(new NullPointerException("Simulated NPE"));
            
            assertThatThrownBy(() -> productSearchService.searchProductsByQuery(query))
                    .isInstanceOf(GeneralSearchException.class)
                    .hasMessage(SearchResponseMessage.SEARCH_SERVICE_ERROR);
            
            verify(productRepository).searchProducts(validQuery);
        }
    }
}
