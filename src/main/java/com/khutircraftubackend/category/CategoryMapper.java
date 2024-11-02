package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	
	CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
	
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	CategoryEntity toCategoryEntity(CategoryRequest request);
	
	@Mapping(target = "parentId", source = "parentCategory.id")
	@Mapping(target = "iconUrl", source = "iconUrl")
	CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
	
	@Mapping(target = "name", source = "request.name")
	@Mapping(target = "description", source = "request.description")
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	void updateCategoryEntity(@MappingTarget CategoryEntity category, CategoryRequest request);
	
	default Collection<CategoryResponse> toCategoryResponse(Collection<CategoryEntity> categoryEntities) {
		
		return categoryEntities.stream()
				.map(this::toCategoryResponse)
				.collect(Collectors.toList());
	}
	
}
