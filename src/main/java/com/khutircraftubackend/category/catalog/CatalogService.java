package com.khutircraftubackend.category.catalog;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.catalog.response.CategoryTreeNode;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {
    
    private final CategoryRepository categoryRepository;
    
    @Cacheable("categoryTree")
    public List<CategoryTreeNode> getCatalogTree() {
        
        Map<Long, CategoryEntity> categoryMap = loadCategoryMap();
        
        return categoryMap.values().stream()
                .filter(category -> category.getParentCategory() == null)
                .map(category -> buildSubtree(category, categoryMap))
                .toList();
        
        
    }
    
    @Cacheable(value = "categoryTree", key = "#p0")
    public CategoryTreeNode getCatalogTree(Long categoryId) {
        
        Map<Long, CategoryEntity> categoryMap = loadCategoryMap();
        
        CategoryEntity currentEntity = categoryMap.get(categoryId);
        
        if (currentEntity == null) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND);
        }
        
        CategoryTreeNode currentNode = new CategoryTreeNode();
        currentNode.setId(currentEntity.getId());
        currentNode.setName(currentEntity.getName());
        
        return buildParentPath(currentNode, categoryMap);
    }
    
    private Map<Long, CategoryEntity> loadCategoryMap() {
        
        return categoryRepository.findAll().stream()
                .collect(Collectors.toMap(CategoryEntity::getId, category -> category));
    }
    
    private CategoryTreeNode buildSubtree(CategoryEntity currentEntity, Map<Long, CategoryEntity> categoryMap) {
        
        CategoryTreeNode currentNode = new CategoryTreeNode();
        currentNode.setId(currentEntity.getId());
        currentNode.setName(currentEntity.getName());
        
        Predicate<CategoryEntity> hasParent = category ->
                category.getParentCategory() != null &&
                        category.getParentCategory().getId().equals(currentEntity.getId());
        
        List<CategoryEntity> children = categoryMap.values().stream()
                .filter(hasParent)
                .toList();
        
        for (CategoryEntity childEntity : children) {
            currentNode.getChildren().add(buildSubtree(childEntity, categoryMap));
        }
        
        return currentNode;
    }
    
    private CategoryTreeNode buildParentPath(CategoryTreeNode currentNode, Map<Long, CategoryEntity> categoryMap) {
        
        CategoryEntity currentEntity = categoryMap.get(currentNode.getId());
        
        if (currentEntity.getParentCategory() != null) {
            CategoryEntity parentEntity = categoryMap.get(currentEntity.getParentCategory().getId());
            
            CategoryTreeNode parentNode = new CategoryTreeNode();
            parentNode.setId(parentEntity.getId());
            parentNode.setName(parentEntity.getName());
            parentNode.getChildren().add(currentNode);
            
            return buildParentPath(parentNode, categoryMap);
        }
        
        return currentNode;
    }
    
}
