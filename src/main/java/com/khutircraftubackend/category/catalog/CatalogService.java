package com.khutircraftubackend.category.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
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
    private final CategoryViewRepository categoryViewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Cacheable("categoryTree")
    public List<CategoryTreeNode> getCatalogTree() {

        List<CategoryEntity> allCategories = categoryRepository.findAll();
        Map<Long, CategoryEntity> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getId, category -> category));

        return allCategories.stream()
                .filter(category -> category.getParentCategory() == null)
                .map(category -> buildCategoryTree(category, categoryMap))
                .toList();
    }

    private CategoryTreeNode buildCategoryTree(CategoryEntity category, Map<Long, CategoryEntity> categoryMap) {
        CategoryTreeNode node = new CategoryTreeNode();
        node.setId(category.getId());
        node.setName(category.getName());

        Predicate<CategoryEntity> hasParent = cat ->
                cat.getParentCategory() != null && cat.getParentCategory().getId().equals(category.getId());

        List<CategoryEntity> children = categoryMap.values().stream()
                .filter(hasParent)
                .toList();

        for (CategoryEntity childEntity : children) {
            node.getChildren().add(buildCategoryTree(childEntity, categoryMap));
        }

        return node;
    }

    @Cacheable("categoryPathTree")
    public CategoryTreeNode getCatalogTree(Long categoryId) {
        CategoryViewEntity categoryViewEntity = categoryViewRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("BLAH!"));

        // TODO: Handle json parsing exception properly.
        try {
            return objectMapper.readValue(categoryViewEntity.getJsonTree(), CategoryTreeNode.class);
        } catch (Exception ex) {
            log.error("Error parsing JSON tree for category ID {}: {}", categoryId, ex.getMessage());
            throw new RuntimeException("Error parsing category tree JSON", ex);
        }
    }
}
