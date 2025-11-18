package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryNameNormalizer;
import com.khutircraftubackend.category.response.CategoryResponse;
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
	@Mapping(target = "slug", source = "request.name", qualifiedByName = "normalizeForSlug")
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
						
						.replaceAll("[^[\\p{L}\\d\\-]]+", "")
						
						.replaceAll("[\\u0027\\u02B9\\u02BB\\u02BC\\u02BE\\u02C8\\u02EE\\u0301\\u0313\\u0315\\u055A\\u05F3\\u07F4\\u07F5\\u1FBF\\u2018\\u2019\\u2032\\uA78C\\uFF07]", "")
						
						.toLowerCase()
						
						).filter(s -> !s.isBlank())
				.collect(Collectors.toCollection(LinkedHashSet::new));

		if (validKeywords.isEmpty()) {
			throw new InvalidSearchQueryException(EMPTY_KEYWORDS_ERROR);
		}

		return String.join(",", validKeywords);
	}
	
	@Named("normalizeForSlug")
	default String normalizeForSlug (String name) {
		
		return CategoryNameNormalizer.normalizeForSlug(name);
	}
	
}
