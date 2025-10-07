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

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final CategoryViewRepository categoryViewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Cacheable("categoryTree")
    public List<CategoryTreeNode> getCatalogTree() {

        List<CategoryEntity> rootCategories = categoryRepository.findAllByParentCategoryIsNull();

        return rootCategories.stream()
                .map(this::traverseCategoryEntity)
                .toList();
    }

    private CategoryTreeNode traverseCategoryEntity(CategoryEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Category entity cannot be null");
        };

        CategoryTreeNode parentNode = new CategoryTreeNode();
        parentNode.setId(entity.getId());
        parentNode.setName(entity.getName());

        List<CategoryEntity> childrenNodes = categoryRepository.findAllByParentCategory_Id(entity.getId());

        if (!childrenNodes.isEmpty()) {
            for (CategoryEntity childEntity : childrenNodes) {
                parentNode.getChildren().add(traverseCategoryEntity(childEntity));
            }
        }
        return parentNode;
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
