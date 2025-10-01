package com.khutircraftubackend.category.path;

import com.khutircraftubackend.category.path.response.CategoryPathItem;
import com.khutircraftubackend.category.path.response.CategoryTreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathService {
    
    private final CategoryViewRepository repo;
    private final CategoryPathMapper mapper;
    
    @Cacheable("categoryTree")
    public List<CategoryTreeNode> getCatalogTree() {
        
        List<CategoryViewEntity> categories = repo.findAll(Sort.by("path"));
        Map<Long, CategoryTreeNode> nodeMap = new LinkedHashMap<>();
        List<CategoryTreeNode> roots = new ArrayList<>();
        
        for (CategoryViewEntity category : categories) {
            CategoryTreeNode node = mapper.toCatalogResponse(category);
            nodeMap.put(node.getId(), node);
        }
        
        for (CategoryViewEntity category : categories) {
            CategoryTreeNode node = nodeMap.get(category.getId());
            
            if (category.getParentId() == null) {
                log.warn("Orphan category (missing parent): id={}", category.getId());
                roots.add(node);
            } else {
                CategoryTreeNode parentNode = nodeMap.get(category.getParentId());
                
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                } else {
                    log.warn("Orphan category (parent not found): id={}, parentId={}", category.getId(), category.getParentId());
                    nodeMap.remove(node.getId());
                }
            }
        }
        return roots;
    }
    
    public List<CategoryPathItem> getCategoryPathItem(Long categoryId) {
        
        return repo.findById(categoryId)
                .map(mapper::toCategoryPathItem)
                .orElseGet(() -> mapper.toCategoryPathItem(null));
    }
    
}
