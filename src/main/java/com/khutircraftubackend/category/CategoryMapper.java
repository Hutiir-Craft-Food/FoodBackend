package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.category.util.CategoryNameNormalizer;
import com.khutircraftubackend.product.ProductMapper;
import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

import static com.khutircraftubackend.search.exception.SearchResponseMessage.EMPTY_KEYWORDS_ERROR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
		uses = {ProductMapper.class, CategoryRepository.class})
public interface CategoryMapper {
	
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	@Mapping(target = "keywords", source = "request.keywords", qualifiedByName = "keywordsToString", resultType = String.class)
	CategoryEntity toCategoryEntity(CategoryRequest request);
	
	@Mapping(target = "parentCategoryId", source = "parentCategory.id")
	@Mapping(target = "iconUrl", source = "iconUrl")
	@Mapping(target = "keywords", source = "keywords", qualifiedByName = "parseKeywords")
	CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
	
	@Mapping(target = "name", source = "request.name")
	@Mapping(target = "description", source = "request.description")
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	@Mapping(target = "keywords", source = "request.keywords", qualifiedByName = "keywordsToString", resultType = String.class)
	@Mapping(target = "slug", ignore = true)
	void updateCategoryEntity(@MappingTarget CategoryEntity category, CategoryRequest request);
	
	Collection<CategoryResponse> toCategoryResponse(Collection<CategoryEntity> categoryEntities);

	@Named("parseKeywords")
	default Set<String> parseKeywords (String keywords) {
		
		if (StringUtils.isEmpty(keywords)) {
			
			return Collections.emptySet();
		}
		return new LinkedHashSet<>(Arrays.stream(keywords.split(",")).toList());
	}

	@Named("keywordsToString")
	default String keywordsToString (Set<String> keywords) {
		
		if (keywords == null || keywords.isEmpty()) {
			
			return null;
		}

		Set<String> validKeywords = keywords.stream()
				.map(s -> s.trim()
						
						.replaceAll("[^\\p{L}\\d\\-]+", "")
						
						.replaceAll("[" + CategoryNameNormalizer.APOSTROPHE_CHARS + "]", "")
						
						.toLowerCase()
						
						).filter(s -> !s.isBlank())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		if (validKeywords.isEmpty()) {
			throw new InvalidSearchQueryException(EMPTY_KEYWORDS_ERROR);
		}

		return String.join(",", validKeywords);
	}

}
