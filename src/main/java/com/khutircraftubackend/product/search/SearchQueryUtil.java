package com.khutircraftubackend.product.search;


import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import com.khutircraftubackend.product.search.exception.SearchResponseMessage;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

@UtilityClass
public class SearchQueryUtil {
    public static String clean(String query) {
        
        if (query.isBlank()) {
            throw new InvalidSearchQueryException(SearchResponseMessage.EMPTY_QUERY_ERROR);
        }
        
        return Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none());
    }
}
