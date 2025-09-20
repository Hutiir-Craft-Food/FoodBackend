package com.khutircraftubackend.category;

import com.khutircraftubackend.category.breadcrumb.response.BreadcrumbResponse;
import com.khutircraftubackend.category.breadcrumb.BreadcrumbService;
import com.khutircraftubackend.category.breadcrumb.response.CatalogResponse;
import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.category.response.CategoryResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryService categoryService;
	private final BreadcrumbService breadcrumbService;
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
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public CategoryResponse createCategory(
			@Valid @ModelAttribute CategoryRequest request,
			@RequestPart(value = "iconFile", required = false) MultipartFile iconFile
	) throws IOException {
		CategoryEntity category = categoryService.createCategory(request, iconFile);

		return categoryMapper.toCategoryResponse(category);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CategoryResponse getCategoryById (@PathVariable Long id) {

		CategoryEntity category = categoryService.findCategoryById(id);

		return categoryMapper.toCategoryResponse(category);
	}

	@PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public CategoryResponse updateCategory (
			@PathVariable Long id,
			@Valid @ModelAttribute CategoryRequest request,
			@RequestPart(value = "iconFile", required = false) MultipartFile iconFile
	) throws IOException {
		CategoryEntity updateCategory = categoryService.updateCategory(id, request, iconFile);

		return categoryMapper.toCategoryResponse(updateCategory);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory (
			@PathVariable Long id,
			@RequestParam(required = false) boolean forceDelete
	) {
		categoryService.deleteCategory(id, forceDelete);
	}

	@PatchMapping("/{id}/keywords")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse updateKeywords (
			@PathVariable Long id,
			@RequestBody @Nullable LinkedHashSet<String> keywords
	) {
		CategoryEntity category = categoryService.updateKeywords(id, keywords);

		return categoryMapper.toCategoryResponse(category);
	}
	
	@GetMapping("/catalog")
	@ResponseStatus(HttpStatus.OK)
	public List<CatalogResponse> getCategoryCatalog() {
		
		return breadcrumbService.getCatalogTree();
	}
	
	@GetMapping("/catalog/{categoryId}")
	@ResponseStatus(HttpStatus.OK)
	public List<BreadcrumbResponse> getCategoryBreadcrumbs(@PathVariable Long categoryId) {
		
		return breadcrumbService.getBreadcrumbs(categoryId);
	}
}
