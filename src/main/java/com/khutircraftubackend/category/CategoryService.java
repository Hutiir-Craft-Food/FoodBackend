package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.CategoryDeletionException;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.storage.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS;
import static com.khutircraftubackend.category.exception.CategoryExceptionMessages.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	private final StorageService storageService;
	
	public CategoryEntity findCategoryById(Long id) {
		
		return categoryRepository.findById(id).orElseThrow(() ->
				new CategoryNotFoundException(CATEGORY_NOT_FOUND));
	}
	

    private String uploadIcon(MultipartFile iconFile) throws IOException {
		
		if (iconFile == null || iconFile.isEmpty()) {
			return "";
		}
		
		return storageService.upload(iconFile);
	}
	
	
	public List<CategoryEntity> getAllRootCategories() {
		
		return categoryRepository.findAllByParentCategoryIsNull();
	}
	
	
	public List<CategoryEntity> getAllByParentCategoryId(Long id) {
		
		return categoryRepository.findAllByParentCategory_Id(id);
	}
	

	@Transactional
	public CategoryEntity createCategory(CategoryRequest request, MultipartFile iconFile) throws IOException {
		
		CategoryEntity category = categoryMapper.toCategoryEntity(request);
		
        category.setIconUrl(uploadIcon(iconFile));
		
		setParentCategory(category, request.parentCategoryId());
		
		return categoryRepository.save(category);
	}
	
	
	@Transactional
	public CategoryEntity updateCategory(Long id, CategoryRequest request,
										 MultipartFile iconFile
	) throws IOException {

		CategoryEntity existingCategory = findCategoryById(id);
        String iconFileUrl = uploadIcon(iconFile);
        existingCategory.setIconUrl(iconFileUrl);
		
		setParentCategory(existingCategory, request.parentCategoryId());

		categoryMapper.updateCategoryEntity(existingCategory, request);

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
			throw new CategoryDeletionException(CATEGORY_HAS_SUBCATEGORIES_OR_PRODUCTS);
		}
	}

	@Transactional
	public CategoryEntity updateKeywords (Long id, Set<String> keywords) {

		CategoryEntity existingCategory = findCategoryById(id);
		
		String keywordsStr = categoryMapper.keywordsToString(keywords);
		existingCategory.setKeywords(keywordsStr);

		return categoryRepository.save(existingCategory);
	}

	private void setParentCategory(CategoryEntity category, Long parentCategoryId) {

		CategoryEntity parentCategory = null;
		
		if (parentCategoryId != null) {
			parentCategory = findCategoryById(parentCategoryId);
		}
		category.setParentCategory(parentCategory);
	}
}
