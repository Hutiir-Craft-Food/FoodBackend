package com.khutircraftubackend.category.request;

import com.khutircraftubackend.category.exception.InvalidCategoryNameException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Locale;

import static com.khutircraftubackend.category.CategoryResponseMessage.NOT_VALID_SYMBOL;
import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.CATEGORY_NAME_INVALID;

@Builder
public record CategoryRequest (
		@NotBlank(message = "Category name cannot be blank")
		@Pattern(regexp = "^([\\p{IsCyrillic}\\d\\s.,:;_\\-+()%&]+|[\\p{IsLatin}\\d\\s.,:;_\\-+()%&]+)$",
				message = NOT_VALID_SYMBOL)//унеможливлення змішування мов в назві
		@Size(max = 255, message = "Назва категорії не може перевищувати 255 символів")
		String name,

		@NotBlank(message = "Description cannot be blank")
		String description,

		@Nullable
		Long parentCategoryId,

		@Nullable
		LinkedHashSet<String> keywords
) {
	public CategoryRequest {
		
		if(name != null) name = normalizeName(name);
	}
	
	private static String normalizeName(String input) {
		
		if(input == null || input.isBlank())
			throw new InvalidCategoryNameException(String.format(CATEGORY_NAME_INVALID, input));
		
		final String clean = Jsoup.clean(HtmlUtils.htmlUnescape(input), Safelist.none());
		
		final String normalized = Normalizer.normalize(clean, Normalizer.Form.NFC);
		
		return normalized
				.strip()
				.toLowerCase(Locale.ROOT)
				.replaceAll("[\\s\\u00A0\\u2007\\u202F]+", " ");
		
	}
	
}
