package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryCreateRequest;
import com.khutircraftubackend.category.request.CategoryUpdateRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	
	@Mapping(target = "name", source = "name")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	CategoryEntity toCategoryEntity(CategoryCreateRequest request);
	
	@Mapping(target = "name", source = "name")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "parentId", source = "parentCategory.id")
	@Mapping(target = "iconUrl", source = "iconUrl")
	CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
	
	@Mapping(target = "name", source = "request.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "description", source = "request.description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateCategoryEntity(@MappingTarget CategoryEntity category, CategoryUpdateRequest request);
	
	default Collection<CategoryResponse> toCategoryResponse(Collection<CategoryEntity> categoryEntities) {
		
		return categoryEntities.stream()
				.map(this::toCategoryResponse)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
}
