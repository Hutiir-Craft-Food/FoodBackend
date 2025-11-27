package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.CategoryCreationException;
import com.khutircraftubackend.category.exception.CategoryDeletionException;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryNameNormalizer;
import com.khutircraftubackend.exception.FileReadingException;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.storage.exception.StorageException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.*;
import static com.khutircraftubackend.storage.StorageResponseMessage.ERROR_SAVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StorageService storageService;

    public CategoryEntity findCategoryById(Long id) {

        return categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }

    public List<CategoryEntity> getAllRootCategories() {

        return categoryRepository.findAllByParentCategoryIsNull();
    }


    public List<CategoryEntity> getAllByParentCategoryId(Long id) {

        return categoryRepository.findAllByParentCategory_Id(id);
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    @Transactional
    public CategoryEntity createCategory(CategoryRequest request, MultipartFile iconFile) {

        String displayName = CategoryNameNormalizer.normalizeForDisplayName(request.name());
        String slug = CategoryNameNormalizer.normalizeForSlug(displayName);
        
        if(categoryRepository.existsBySlug(slug)) {
            throw new CategoryCreationException(String.format(CATEGORY_ALREADY_EXISTS, displayName));
        }
        
        CategoryEntity category = categoryMapper.toCategoryEntity(request);
        category.setName(displayName);
        category.setSlug(slug);
        setParentCategory(category, request.parentCategoryId());

        category = saveCategoryWithIntegrityCheck(category);

        String iconLink = uploadIcon(iconFile);

        if (iconLink != null) category.setIconUrl(iconLink);

        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    @Transactional
    public CategoryEntity updateCategory(Long id, CategoryRequest request,
                                         MultipartFile iconFile) {

        CategoryEntity existingCategory = findCategoryById(id);

        if (iconFile != null && !iconFile.isEmpty()) {

            String oldIconUrl = existingCategory.getIconUrl();
            String newIconUrl = uploadIcon(iconFile);
            existingCategory.setIconUrl(newIconUrl);

            if (oldIconUrl != null && !oldIconUrl.equals(newIconUrl)) {
                deleteIcon(oldIconUrl);
            }
        }
        setParentCategory(existingCategory, request.parentCategoryId());

        categoryMapper.updateCategoryEntity(existingCategory, request);

        return saveCategoryWithIntegrityCheck(existingCategory);
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    @Transactional
    public void deleteCategory(Long id, boolean forceDelete) {

        List<CategoryEntity> childCategories = categoryRepository.findAllByParentCategory_Id(id);

        if (childCategories.isEmpty()) {
            deleteCategoryWithIcon(id);

        } else if (forceDelete) {
            for (CategoryEntity child : childCategories) {
                deleteCategory(child.getId(), true);
            }
            deleteCategoryWithIcon(id);
        } else {
            throw new CategoryDeletionException(CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS);
        }
    }

    @Transactional
    public CategoryEntity updateKeywords(Long id, Set<String> keywords) {

        CategoryEntity existingCategory = findCategoryById(id);

        String keywordsStr = categoryMapper.keywordsToString(keywords);
        existingCategory.setKeywords(keywordsStr);

        return categoryRepository.save(existingCategory);
    }

    private CategoryEntity saveCategoryWithIntegrityCheck(CategoryEntity category) {

        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation while saving category '{}': {}", category.getName(), e.getMessage());

            throw new CategoryCreationException(String.format(CATEGORY_ALREADY_EXISTS, category.getName()));
        }
    }

    private void setParentCategory(CategoryEntity category, Long parentCategoryId) {

        CategoryEntity parentCategory = null;

        if (parentCategoryId != null) {
            parentCategory = findCategoryById(parentCategoryId);
        }
        category.setParentCategory(parentCategory);
    }

    private String uploadIcon(MultipartFile iconFile) {

        if (iconFile == null || iconFile.isEmpty()) return null;

        String link;
        try {
            link = storageService.upload(iconFile);
        } catch (FileReadingException ex) {
            throw new StorageException(ERROR_SAVE);
        }

        return link;
    }

    private void deleteIcon(String iconUrl) {

        if (iconUrl == null || iconUrl.isBlank()) return;

        storageService.deleteByUrl(iconUrl);
    }

    private void deleteCategoryWithIcon(Long id) {

        CategoryEntity category = findCategoryById(id);
        categoryRepository.deleteById(id);
        deleteIcon(category.getIconUrl());
    }
    
}