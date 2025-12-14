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
    
    public static final String APOSTROPHE_CHARS = "\u0027\u02B9\u02BB\u02BC\u02BE\u02C8\u02EE\u0301\u0313\u0315\u055A\u05F3\u07F4\u07F5\u1FBF\u2018\u2019\u2032\uA78C\uFF07";
    public static final String CATEGORY_NAME_PATTERN = "^[а-щА-ЩЬьЮюЯяЇїІіЄєҐґ\\d\\s.,:;_+\\-()%&" + APOSTROPHE_CHARS + "]+$";
    private static final String UNICODE_SPACES_CLASS = "[\\s\\u00A0\\u2007\\u202F]";
    
    public static String normalizeForSlug(String input) {
        
        if (input == null) {
            throw new InvalidCategoryNameException(String.format(CATEGORY_NAME_INVALID, input));
        }
        
        String cleaned = Jsoup.clean(input, Safelist.none());
        String nfc = Normalizer.normalize(cleaned, Normalizer.Form.NFC)
                .strip()
                .toLowerCase(Locale.ROOT);
        
        String slug = nfc
                .replaceAll(UNICODE_SPACES_CLASS + "+", "-")
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
                .replaceAll("[" + APOSTROPHE_CHARS + "]+", "ʼ")// Уніфікація апострофів до U+02BC
                .replaceAll(UNICODE_SPACES_CLASS + "+", " ");// Уніфікація пробілів
    }
    
}
