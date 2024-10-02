package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.category.CategoryDeletionException;
import com.khutircraftubackend.category.exception.category.CategoryExceptionMessages;
import com.khutircraftubackend.category.exception.category.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryCreateRequest;
import com.khutircraftubackend.category.request.CategoryUpdateRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import com.khutircraftubackend.product.ProductRepository;
import com.khutircraftubackend.product.image.FileConverterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileConverterService fileConverterService;
    private final ProductRepository productRepository;

    private String handleIcon(MultipartFile iconFile) throws IOException {
        if (iconFile == null) {
            return "";
        }
        return fileConverterService.convert(iconFile);
    }

    private CategoryEntity findCategoryById(Long id) {

        return categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));
    }

    public List<CategoryResponse> getAllRootCategories() {
        return categoryRepository.findAllByParentCategoryIsNull().stream().map(
                categoryMapper::toCategoryResponse
        ).collect(Collectors.toList());
    }

    public List<CategoryResponse> getAllByParentCategoryId(Long id) {
        return categoryRepository.findAllByParentCategory_Id(id).stream().map(
                categoryMapper::toCategoryResponse
        ).collect(Collectors.toList());
    }

    @Transactional
    public CategoryEntity createCategory(CategoryCreateRequest request, MultipartFile iconFile) throws IOException {

        CategoryEntity category = categoryMapper.toCategoryEntity(request);
        category.setIconUrl(handleIcon(iconFile));

        if (request.parentCategoryId() != null) {
            CategoryEntity parentCategory = findCategoryById(request.parentCategoryId());

            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }
        return categoryRepository.save(category);
    }


    @Transactional
    public CategoryEntity updateCategory(Long id, CategoryUpdateRequest request, MultipartFile iconFile) throws IOException {
        CategoryEntity existingCategory = findCategoryById(id);

        if (iconFile != null && !iconFile.isEmpty()) {
            existingCategory.setIconUrl(handleIcon(iconFile));
        } else if (request.iconFile() != null) {
            existingCategory.setIconUrl(handleIcon(iconFile));
        }
        categoryMapper.updateCategoryEntity(existingCategory, request);

        if (request.parentCategoryId() != null) {
            CategoryEntity parentCategory = findCategoryById(request.parentCategoryId());
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        deleteCategory(id, false);
    }

    @Transactional
    public void deleteCategory(Long id, boolean forceDelete) {
        List<CategoryEntity> childCategories = categoryRepository.findAllByParentCategory_Id(id);

        if (childCategories.isEmpty()) {
            categoryRepository.deleteById(id);
        }


        if (!childCategories.isEmpty() && forceDelete) {
            for (CategoryEntity child : childCategories) {
                deleteCategory(child.getId(), forceDelete);
            }
            categoryRepository.deleteById(id);
        } else if(!childCategories.isEmpty()){
            throw new CategoryDeletionException(CategoryExceptionMessages.CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS);
        }
    }
}
