package com.khutircraftubackend.product.search;


import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;

@UtilityClass
public class SearchQueryUtil {
    public static String clean(String query) {
    
        String cleaned = Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none());
    
        if (cleaned.trim().isEmpty()) {
            throw new InvalidSearchQueryException(EMPTY_QUERY_ERROR);
        }
    
        return cleaned;
    }
}
