package com.khutircraftubackend.search;


import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

import static com.khutircraftubackend.search.exception.SearchResponseMessage.EMPTY_QUERY_ERROR;

@UtilityClass
public class SearchQueryUtil {

    public static String clean (String query) {
        if (query.isBlank()) {
            throw new InvalidSearchQueryException(EMPTY_QUERY_ERROR);
        }

        return Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none());
    }
}
