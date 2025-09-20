package com.khutircraftubackend.category.breadcrumb;

import com.khutircraftubackend.category.breadcrumb.response.BreadcrumbResponse;
import com.khutircraftubackend.category.breadcrumb.response.CatalogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BreadcrumbService {
    
    private final CategoryViewRepository repo;
    private final BreadcrumbMapper mapper;
    
    public List<CatalogResponse> getCatalogTree() {
        
        List<CategoryViewEntity> categories = repo.findAll();
        Map<Long, CatalogResponse> nodeMap = new HashMap<>();
        List<CatalogResponse> roots = new ArrayList<>();
        
        for (CategoryViewEntity category : categories) {
            CatalogResponse node = mapper.toCatalogResponse(category);
            nodeMap.put(node.getId(), node);
        }
        
        for (CategoryViewEntity category : categories) {
            CatalogResponse node = nodeMap.get(category.getId());
            
            if (category.getParentId() == null) {
                roots.add(node);
            } else {
                CatalogResponse parentNode = nodeMap.get(category.getParentId());
                
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                } else {
                    nodeMap.remove(node.getId());
                }
            }
        }
        return roots;
    }
    
    public List<BreadcrumbResponse> getBreadcrumbs(Long categoryId) {
        
        return repo.findById(categoryId)
                .map(mapper::toBreadcrumbs)
                .orElseGet(() -> List.of(mapper.toRootBreadcrumbResponse()));
    }
    
}
