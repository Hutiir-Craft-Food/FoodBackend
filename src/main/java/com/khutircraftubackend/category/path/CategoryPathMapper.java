package com.khutircraftubackend.category.path;

import com.khutircraftubackend.category.path.response.CategoryPathItem;
import com.khutircraftubackend.category.path.response.CategoryTreeNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface CategoryPathMapper {
    
    @Mapping(target = "children", ignore = true)
    CategoryTreeNode toCatalogResponse(CategoryViewEntity entity);
    
    default CategoryPathItem toCategoryPathItem(Long id, String name) {
        
        return new CategoryPathItem(id, name);
    }

    
    default List<CategoryPathItem> toCategoryPathItem(CategoryViewEntity entity) {
        
        List<CategoryPathItem> categoryPathItems = new ArrayList<>();
        categoryPathItems.add(new CategoryPathItem(0L, "Головна"));
        
        if (entity == null || entity.getPathIds() == null || entity.getPathNames() == null) {
            return categoryPathItems;
        }
        
        List<String> idTokens = Arrays.stream(entity.getPathIds().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        
        List<String> nameTokens = Arrays.stream(entity.getPathNames().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        
        int size = Math.min(idTokens.size(), nameTokens.size());//запобігаємо IndexOutOfBoundsException
        
        for (int i = 0; i < size; i++) {
            categoryPathItems.add(toCategoryPathItem(
                    Long.valueOf(idTokens.get(i)),
                    nameTokens.get(i)
            ));
        }
        if(idTokens.size() != nameTokens.size()){
            LoggerFactory.getLogger(CategoryPathMapper.class)
                    .warn("Mismatched pathIds and pathNames lengths for category id={}", entity.getId());
        }
        
        return categoryPathItems;
    }
}
