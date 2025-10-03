package com.khutircraftubackend.category.path;

import com.khutircraftubackend.category.path.response.CategoryPathItem;
import com.khutircraftubackend.category.path.response.CategoryTreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathService {
    
    private final CategoryViewRepository repo;
    
    @Cacheable("categoryTree")
    public List<CategoryTreeNode> getCatalogTree() {
        
        List<CategoryViewEntity> categories = repo.findAll(Sort.by("path"));
        Map<Long, CategoryTreeNode> nodeMap = new LinkedHashMap<>();
        List<CategoryTreeNode> roots = new ArrayList<>();
        
        for (CategoryViewEntity category : categories) {
            CategoryTreeNode node = new CategoryTreeNode(
                    category.getId(),
                    category.getName(),
                    new ArrayList<>()
            );
            nodeMap.put(node.getId(), node);
        }
        
        for (CategoryViewEntity category : categories) {
            CategoryTreeNode node = nodeMap.get(category.getId());
            
            if (category.getParentId() == null) {
                roots.add(node);
            } else {
                CategoryTreeNode parentNode = nodeMap.get(category.getParentId());
                
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                } else {
                    nodeMap.remove(node.getId());
                }
            }
        }
        return roots;
    }
    
    private CategoryPathItem buildPathTree(CategoryViewEntity entity) {
        String[] ids = entity.getPathIds().split(",");
        String[] names = entity.getPathNames().split(",");
        
        int size = Math.min(ids.length, names.length);
        
        if (size == 0) return null;
        
        CategoryPathItem current = CategoryPathItem.leaf(
                Long.parseLong(ids[size - 1].trim()),
                names[size - 1].trim()
        );
        
        for (int i = size - 2; i >= 0; i--) {
            current = CategoryPathItem.withChild(
                    Long.parseLong(ids[i].trim()),
                    names[i].trim(),
                    current
            );
        }
        
        return current;
    }
    
    public CategoryPathItem getCategoryPathItem(Long categoryId) {
        
        
        return repo.findById(categoryId)
                .map(this::buildPathTree)
                .orElse(null);
        
    }
}
