package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
	
	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;
	
	@GetMapping
	public Collection<CategoryResponse> getAllRootCategories() {
		
		List<CategoryEntity> categoryEntities = categoryService.getAllRootCategories();
		
		return categoryMapper.toCategoryResponse(categoryEntities);
	}
	
	@GetMapping("/parent-id/{id}")
	public Collection<CategoryResponse> getAllCategoriesByParentId(
			@PathVariable Long id) {
		
		Collection<CategoryEntity> categoryEntities = categoryService.getAllByParentCategoryId(id);
		
		return categoryMapper.toCategoryResponse(categoryEntities);
	}
	
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ResponseStatus(HttpStatus.OK)
	public CategoryResponse createCategory(
			@Valid @ModelAttribute CategoryRequest request,
			@RequestPart(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {
		
		CategoryEntity category = categoryService.createCategory(request, iconFile);
		
		return categoryMapper.toCategoryResponse(category);
	}
	
	@PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ResponseStatus(HttpStatus.OK)
	public CategoryResponse updateCategory(
			@PathVariable Long id,
			@Valid @ModelAttribute CategoryRequest request,
			@RequestPart(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {
		
		CategoryEntity updateCategory = categoryService.updateCategory(id, request, iconFile);
		
		return categoryMapper.toCategoryResponse(updateCategory);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(
			@PathVariable Long id,
			@RequestParam(required = false) boolean forceDelete) {
		
		categoryService.deleteCategory(id, forceDelete);
	}
	
}
