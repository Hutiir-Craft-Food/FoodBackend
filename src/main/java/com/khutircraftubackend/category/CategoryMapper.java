package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.product.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
		uses = {ProductMapper.class})
public interface CategoryMapper {
	
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	CategoryEntity toCategoryEntity(CategoryRequest request);
	
	@Mapping(target = "parentCategoryId", source = "parentCategory.id")
	@Mapping(target = "iconUrl", source = "iconUrl")
	CategoryResponse toCategoryResponse(CategoryEntity categoryEntity);
	
	@Mapping(target = "name", source = "request.name")
	@Mapping(target = "description", source = "request.description")
	@Mapping(target = "parentCategory.id", source = "request.parentCategoryId")
	void updateCategoryEntity(@MappingTarget CategoryEntity category, CategoryRequest request);
	
	Collection<CategoryResponse> toCategoryResponse(Collection<CategoryEntity> categoryEntities);
	
}
