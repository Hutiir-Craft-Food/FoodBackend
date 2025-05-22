package com.khutircraftubackend.search;

import com.khutircraftubackend.search.exception.GenericSearchException;
import com.khutircraftubackend.search.response.ProductSearchView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.khutircraftubackend.search.exception.SearchResponseMessage.SEARCH_SERVICE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    
    public List<ProductSearchView> searchProductsByQuery (String query) {
       
        String validQuery = SearchQueryUtil.clean(query);
        try {
            return searchRepository.searchProducts(validQuery.toLowerCase());
        } catch (GenericJDBCException e) {
            log.error("Critical search error: {}", e.getMessage());
            throw new GenericSearchException(SEARCH_SERVICE_ERROR);
        }
    }
}
