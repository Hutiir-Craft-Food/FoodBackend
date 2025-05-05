package com.khutircraftubackend.product.search;


import com.khutircraftubackend.product.search.exception.InvalidSearchQueryException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

import static com.khutircraftubackend.product.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;

@UtilityClass
public class SearchQueryUtil {
    public static String clean(String query) {
    
        if (StringUtils.isEmpty(query)) {
            throw new InvalidSearchQueryException(EMPTY_QUERY_ERROR);
        }
        
        return Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none());
    }
}
