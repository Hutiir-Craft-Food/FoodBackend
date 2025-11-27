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
        
        if (input == null) {
            throw new InvalidCategoryNameException(String.format(CATEGORY_NAME_INVALID, input));
        }
        
        String cleaned = Jsoup.clean(input, Safelist.none());
        String nfc = Normalizer.normalize(cleaned, Normalizer.Form.NFC)
                .strip()
                .toLowerCase(Locale.ROOT);
        
        String slug = nfc
                .replaceAll("[\\s\\u00A0\\u2007\\u202F]+", "-")
                .replaceAll("[^а-щьюяїієґ\\d\\-]", "")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-)|(-$)", "");
        
        if (slug.isBlank()) {
            throw new InvalidCategoryNameException(String.format(CATEGORY_NAME_INVALID, input));
        }
        
        return slug;
    }
    
    public static String normalizeForDisplayName(String input) {
        
        if (input == null) return null;
        
        String cleaned = Jsoup.clean(input, Safelist.none());
        String nfc = Normalizer.normalize(cleaned, Normalizer.Form.NFC);
        
        return nfc
                .strip()
                .replaceAll("[\\s\\u00A0\\u2007\\u202F]+", " ");
    }
    
}
