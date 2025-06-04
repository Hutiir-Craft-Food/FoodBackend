package com.khutircraftubackend.search.exception;

public class SearchResponseMessage {
    public static final String EMPTY_QUERY_ERROR = "Пошуковий запит не може бути порожнім.";
    public static final String SEARCH_SERVICE_ERROR = "Неможливо виконати пошук. Спробуйте пізніше.";
    public static final String EMPTY_KEYWORDS_ERROR = "Ключові слова не можуть бути порожніми";
    
    private SearchResponseMessage() {
    }
}


