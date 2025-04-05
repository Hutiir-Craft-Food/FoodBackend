package com.khutircraftubackend.search;


import jakarta.validation.constraints.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

public record ProductSearchQuery(
        @Pattern(regexp = "^[\\p{L}\\d\\s_%+-]*$", message = SearchResponseMessage.NOT_VALID_SYMBOL
        ) String query) {
    public ProductSearchQuery(String query) {
        this.query = Jsoup.clean(HtmlUtils.htmlUnescape(query), Safelist.none())
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
