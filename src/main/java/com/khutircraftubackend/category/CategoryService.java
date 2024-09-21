package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryCreateRequest;
import com.khutircraftubackend.category.request.CategoryResponse;
import com.khutircraftubackend.category.request.CategoryUpdateRequest;
import com.khutircraftubackend.exception.category.CategoryExceptionMessages;
import com.khutircraftubackend.exception.category.CategoryNotFoundException;
import com.khutircraftubackend.product.image.FileConverterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileConverterService fileConverterService;

    private String handleIcon(MultipartFile iconFile) throws IOException {
        if (iconFile == null) {
            return null;
        }
        return fileConverterService.convert(iconFile);
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

        CategoryEntity parentCategory = Optional.ofNullable(request.parentCategoryId())
                .flatMap(categoryRepository::findById)
                .orElse(null);

        category.setParentCategory(parentCategory);

        return categoryRepository.save(category);
    }

    @Transactional
    public CategoryEntity updateCategory(Long id, CategoryUpdateRequest request, MultipartFile iconFile) throws IOException {
        CategoryEntity existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        if (iconFile != null && !iconFile.isEmpty()) {
            existingCategory.setIconUrl(handleIcon(iconFile));
        } else if (request.iconFile() != null) {
                existingCategory.setIconUrl(handleIcon(iconFile));
            }
        categoryMapper.updateCategoryEntity(existingCategory, request);

        if (request.parentCategoryId() != null) {
            CategoryEntity parentCategory = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        List<CategoryEntity> childCategories = categoryRepository.findAllByParentCategory_Id(id);

        if (category.getParentCategory() != null) {
            throw new IllegalStateException(CategoryExceptionMessages.CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS);
        }

        if (/*category.getParentCategory().getId() == null ||*/!childCategories.isEmpty()) {

            for (CategoryEntity child : childCategories) {

                deleteCategory(child.getId());
            }
        }
        categoryRepository.deleteById(id);
    }

}
