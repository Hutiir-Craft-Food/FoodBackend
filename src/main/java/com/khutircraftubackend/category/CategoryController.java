package com.khutircraftubackend.category;

import com.khutircraftubackend.category.request.CategoryCreateRequest;
import com.khutircraftubackend.category.request.CategoryResponse;
import com.khutircraftubackend.category.request.CategoryUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/")
    public List<CategoryResponse> getAllRootCategories() {
        return categoryService.getAllRootCategories();
    }

    @GetMapping("/parent-id/{id}")
    public List<CategoryResponse> getAllCategoriesByParentId(@PathVariable Long id) {
        return categoryService.getAllByParentCategoryId(id);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse createCategory(
            @ModelAttribute CategoryCreateRequest request,
            @RequestPart(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {
        log.info("Received request: {}", request);
        CategoryEntity category = categoryService.createCategory(request, iconFile);

        return categoryMapper.toCategoryResponse(category);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse updateCategory(@PathVariable Long id,
                                           @ModelAttribute CategoryUpdateRequest request,
                                           @RequestPart(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {

        CategoryEntity updateCategory = categoryService.updateCategory(id, request, iconFile);

        return categoryMapper.toCategoryResponse(updateCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
