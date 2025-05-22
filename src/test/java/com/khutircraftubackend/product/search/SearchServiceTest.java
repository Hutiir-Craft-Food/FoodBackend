package com.khutircraftubackend.product.search;

import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.search.SearchService;
import com.khutircraftubackend.search.exception.SearchResponseMessage;
import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SearchService searchService;

    // TODO: add positive tests

    @Nested
    @DisplayName("Search Product Operations")
    class SearchProducts {
        
        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should throw InvalidSearchQueryException for blank inputs")
        void shouldThrowInvalidSearchQueryExceptionForBlankInputs(String query) {
            InvalidSearchQueryException exception = assertThrows(
                    InvalidSearchQueryException.class,
                    () -> searchService.searchProductsByQuery(query)
            );
            assertEquals(SearchResponseMessage.EMPTY_QUERY_ERROR, exception.getMessage());
        }
    }
}
