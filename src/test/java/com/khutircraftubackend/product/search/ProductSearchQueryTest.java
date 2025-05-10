package com.khutircraftubackend.product.search;

import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
class ProductSearchQueryTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductSearchService productSearchService;
    
    @Nested
    @DisplayName("Search Product Operations")
    class SearchProducts {
        @DisplayName("Should throw InvalidSearchQueryException for blank inputs")
        @ParameterizedTest
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldThrowInvalidSearchQueryExceptionForBlankInputs(String query) {
            
            // Act
            InvalidSearchQueryException exception = assertThrows(InvalidSearchQueryException.class, () -> productSearchService.searchProductsByQuery(query, 0, 4));
            
            // Assert
            assertEquals(EMPTY_QUERY_ERROR, exception.getMessage());
        }
        
        @DisplayName("Should throw InvalidSearchQueryException when query is null")
        @Test
        void shouldThrowExceptionForNullQuery() {
            
            // Act
            InvalidSearchQueryException exception = assertThrows(InvalidSearchQueryException.class, () -> productSearchService.searchProductsByQuery(null, 0, 4));
            
            // Assert
            assertEquals(EMPTY_QUERY_ERROR, exception.getMessage());
        }
        
        @DisplayName("Should calculate correct page number from offset and limit")
        @Test
        void shouldCalculatePageCorrectly() {
            
            // Arrange
            String query = "valid";
            int offset = 8;
            int limit = 4;
            when(productRepository.searchProducts(anyString(), any())).thenReturn(Page.empty());
            
            // Act
            productSearchService.searchProductsByQuery(query, offset, limit);
            
            // Assert
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(productRepository).searchProducts(anyString(), pageableCaptor.capture());
            Pageable capturePageable = pageableCaptor.getValue();
            
            assertEquals(2, capturePageable.getPageNumber(), "Expected page number is 2 for offset=8 and limit=4");
        }
    }
}
