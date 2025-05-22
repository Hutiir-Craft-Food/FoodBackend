package com.khutircraftubackend.search;


import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import com.khutircraftubackend.search.exception.SearchResponseMessage;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

@UtilityClass
public class SearchQueryUtil {

    public static String clean (String query) {
        
        if (query.isBlank()) {
            throw new InvalidSearchQueryException(SearchResponseMessage.EMPTY_QUERY_ERROR);
        }

        return Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none());
    }
}
