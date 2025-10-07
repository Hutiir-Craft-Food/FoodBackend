package com.khutircraftubackend.category.catalog;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.catalog.response.CategoryTreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CategoryRepository categoryRepository;
    
    @Cacheable("catalogTree")
    public List<CategoryTreeNode> getCatalogTree() {

        List<CategoryEntity> allCategories = categoryRepository.findAll();
        Map<Long, CategoryEntity> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getId, category -> category));

        return allCategories.stream()
                .filter(category -> category.getParentCategory() == null)
                .map(category -> buildCatalogTree(category, categoryMap))
                .toList();
    }

    @Cacheable("catalogTree")
    public CategoryTreeNode getCatalogTree(Long categoryId) {

        List<CategoryEntity> allCategories = categoryRepository.findAll();
        Map<Long, CategoryEntity> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getId, category -> category));

        CategoryEntity category = categoryMap.get(categoryId);
        CategoryTreeNode currentNode = new CategoryTreeNode();
        currentNode.setId(category.getId());
        currentNode.setName(category.getName());

        return buildCategoryTree(currentNode, categoryMap);
    }

    private CategoryTreeNode buildCatalogTree(CategoryEntity currentEntity, Map<Long, CategoryEntity> categoryMap) {
        CategoryTreeNode currentNode = new CategoryTreeNode();
        currentNode.setId(currentEntity.getId());
        currentNode.setName(currentEntity.getName());

        Predicate<CategoryEntity> hasParent = category ->
                category.getParentCategory() != null && category.getParentCategory().getId().equals(currentEntity.getId());

        List<CategoryEntity> children = categoryMap.values().stream()
                .filter(hasParent)
                .toList();

        for (CategoryEntity childEntity : children) {
            currentNode.getChildren().add(buildCatalogTree(childEntity, categoryMap));
        }

        return currentNode;
    }

    private CategoryTreeNode buildCategoryTree(CategoryTreeNode currentNode, Map<Long, CategoryEntity> categoryMap) {
        CategoryEntity currentEntity = categoryMap.get(currentNode.getId());

        if (currentEntity.getParentCategory() != null) {
            CategoryEntity parentEntity = categoryMap.get(currentEntity.getParentCategory().getId());

            CategoryTreeNode parentNode = new CategoryTreeNode();
            parentNode.setId(parentEntity.getId());
            parentNode.setName(parentEntity.getName());
            parentNode.getChildren().add(currentNode);
            return buildCategoryTree(parentNode, categoryMap);
        }

        return currentNode;
    }
}
