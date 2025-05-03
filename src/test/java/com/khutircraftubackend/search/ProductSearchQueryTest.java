package com.khutircraftubackend.search;

import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.search.ProductSearchService;
import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import com.khutircraftubackend.product.search.exception.SearchResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
class ProductSearchQueryTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductSearchService productSearchService;
    
    @Nested
    @DisplayName("Search Product Operations")
    class SearchProducts {
        
        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should throw InvalidSearchQueryException for blank inputs")
        void shouldThrowInvalidSearchQueryExceptionForBlankInputs(String query) {
            InvalidSearchQueryException exception = assertThrows(
                    InvalidSearchQueryException.class,
                    () -> productSearchService.searchProductsByQuery(query, 0, 4)
            );
            assertEquals(SearchResponseMessage.EMPTY_QUERY_ERROR, exception.getMessage());
        }
    }
}
