package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.category.CategoryDeletionException;
import com.khutircraftubackend.category.exception.category.CategoryExceptionMessages;
import com.khutircraftubackend.category.exception.category.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.product.image.CloudinaryServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	private final CloudinaryServiceImpl cloudinaryService;
	public CategoryEntity findCategoryById(Long id) {
		
		return categoryRepository.findById(id).orElseThrow(() ->
				new CategoryNotFoundException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));
	}
	
	private String handleIcon(MultipartFile iconFile) throws IOException {
		
		if (iconFile == null) {
			return "";
		}
		return cloudinaryService.uploadResource(iconFile);
		
	}
	
	public List<CategoryEntity> getAllRootCategories() {
		
		return categoryRepository.findAllByParentCategoryIsNull();
	}
	
	public List<CategoryEntity> getAllByParentCategoryId(Long id) {
		
		return categoryRepository.findAllByParentCategory_Id(id);
	}
	
	private void setParentCategory(CategoryEntity category, Long parentCategoryId) {
		
		if (parentCategoryId != null) {
			CategoryEntity parentCategory = findCategoryById(parentCategoryId);
			category.setParentCategory(parentCategory);
		} else {
			category.setParentCategory(null);
		}
		
	}
	
	@Transactional
	public CategoryEntity createCategory(CategoryRequest request, MultipartFile iconFile) throws IOException {
		
		CategoryEntity category = categoryMapper.toCategoryEntity(request);
		
		category.setIconUrl(handleIcon(iconFile));
		
		setParentCategory(category, request.parentCategoryId());
		
		return categoryRepository.save(category);
	}
	
	
	@Transactional
	public CategoryEntity updateCategory(Long id, CategoryRequest request,
										 MultipartFile iconFile) throws IOException {
		
		CategoryEntity existingCategory = findCategoryById(id);
		
		if (iconFile != null && !iconFile.isEmpty()) {
			existingCategory.setIconUrl(handleIcon(iconFile));
		} else if (iconFile != null) {
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
	
	
	@Transactional
	public void deleteCategory(Long id, boolean forceDelete) {
		
		List<CategoryEntity> childCategories = categoryRepository.findAllByParentCategory_Id(id);
		
		if (childCategories.isEmpty()) {
			categoryRepository.deleteById(id);
		} else if (forceDelete) {
			for (CategoryEntity child : childCategories) {
				deleteCategory(child.getId(), true);
			}
			categoryRepository.deleteById(id);
		} else {
			throw new CategoryDeletionException(CategoryExceptionMessages.CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS);
		}
	}
	
}
