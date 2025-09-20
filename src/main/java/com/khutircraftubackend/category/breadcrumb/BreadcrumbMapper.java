package com.khutircraftubackend.category.breadcrumb;

import com.khutircraftubackend.category.breadcrumb.response.BreadcrumbResponse;
import com.khutircraftubackend.category.breadcrumb.response.CatalogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface BreadcrumbMapper {
    
    @Mapping(target = "children", ignore = true)
    CatalogResponse toCatalogResponse(CategoryViewEntity entity);
    
    default BreadcrumbResponse toBreadcrumbResponse(Long id, String label) {
        
        return new BreadcrumbResponse(id, label, "/categories/catalog/" + id);
    }
    
    default BreadcrumbResponse toRootBreadcrumbResponse() {
        
        return new BreadcrumbResponse(0L, "Головна", "/");
    }
    
    default List<BreadcrumbResponse> toBreadcrumbs(CategoryViewEntity entity) {
        
        List<BreadcrumbResponse> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(toRootBreadcrumbResponse());
        
        if (entity == null || entity.getPathIds() == null || entity.getPathNames() == null) {
            return breadcrumbs;
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
            breadcrumbs.add(toBreadcrumbResponse(
                    Long.valueOf(idTokens.get(i)),
                    nameTokens.get(i)
            ));
        }
        return breadcrumbs;
    }
}
