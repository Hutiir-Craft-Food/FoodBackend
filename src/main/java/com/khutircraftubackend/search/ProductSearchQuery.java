package com.khutircraftubackend.search;


import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;
public record ProductSearchQuery(String query) {
    public ProductSearchQuery(String query) {
        String cleaned = Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none())
                .trim()
                .replaceAll("\\s{2,}", " ");
        
        if (!cleaned.matches("^[\\p{L}\\d\\s_ʼ'.,\\-]*$")) {
            throw new InvalidSearchQueryException(SearchResponseMessage.NOT_VALID_SYMBOL);
        }
        this.query = cleaned;
    }
}
