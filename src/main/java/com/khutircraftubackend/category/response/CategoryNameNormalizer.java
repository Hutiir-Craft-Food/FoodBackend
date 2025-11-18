package com.khutircraftubackend.category.response;

import com.khutircraftubackend.category.exception.InvalidCategoryNameException;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.text.Normalizer;
import java.util.Locale;

import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.CATEGORY_NAME_INVALID;

@UtilityClass
public class CategoryNameNormalizer {
    
    public static String normalizeForSlug(String input) {
        
        if (input == null || input.isBlank()) {
            throw new InvalidCategoryNameException(String.format(CATEGORY_NAME_INVALID, input));
        }

        String cleaned = Jsoup.clean(input, Safelist.none());
        String nfc = Normalizer.normalize(cleaned, Normalizer.Form.NFC);
        
        return nfc
                .strip()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s\\u00A0\\u2007\\u202F]+", "-")
                .replaceAll("[^\\p{L}\\p{Nd}\\-]", "")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-)|(-$)", "");
    }
    
    public static String normalizeForDisplayName(String input) {
        
        if(input == null) return null;
        
        String cleaned = Jsoup.clean(input, Safelist.none());
        String nfc = Normalizer.normalize(cleaned, Normalizer.Form.NFC);
        
        return nfc
                .strip()
                .replaceAll("[\\s\\u00A0\\u2007\\u202F]+", " ");
    }
    
}
