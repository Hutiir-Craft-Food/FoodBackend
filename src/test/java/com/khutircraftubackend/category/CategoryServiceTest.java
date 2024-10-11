package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.category.CategoryDeletionException;
import com.khutircraftubackend.category.exception.category.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryCreateRequest;
import com.khutircraftubackend.category.request.CategoryUpdateRequest;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.image.exception.file.InvalidFileFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;
    
    @Mock
    private CategoryController categoryController;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private FileConverterService fileConverterService;
    
    @Test
    void getAllRootCategories() {
        CategoryEntity rootCategory = new CategoryEntity();
        rootCategory.setName("TestName");
        rootCategory.setIconUrl(null);
        rootCategory.setDescription("TestDescription");
        rootCategory.setParentCategory(null);

        List<CategoryEntity> categoryEntityList = Collections.singletonList(rootCategory);

        when(categoryRepository.findAllByParentCategoryIsNull()).thenReturn(categoryEntityList);

        List<CategoryEntity> result = categoryService.getAllRootCategories();

        assertEquals(1, result.size());

        verify(categoryRepository).findAllByParentCategoryIsNull();
    }
    
    @Test
    void getAllCategoriesByParentId() {
        Long id = 1L;

        CategoryEntity parentCategory = new CategoryEntity();
        parentCategory.setId(2L);
        parentCategory.setName("TestParentCategory");

        CategoryEntity childCategory = new CategoryEntity();
        childCategory.setId(id);
        childCategory.setName("TestChildCategory");
        childCategory.setParentCategory(parentCategory);

        List<CategoryEntity> categoryEntityList = Collections.singletonList(childCategory);

        when(categoryRepository.findAllByParentCategory_Id(id)).thenReturn(categoryEntityList);

        List<CategoryEntity> result = categoryService.getAllByParentCategoryId(id);

        assertEquals(1, result.size());
        assertEquals(childCategory, result.get(0));

        verify(categoryRepository).findAllByParentCategory_Id(id);
    }
    
    @Test
    void createCategory_ShouldCreateCategoryWithoutParent() throws IOException {
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .parentCategoryId(null)
                .build();

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(request.name());
        categoryEntity.setDescription(request.description());

        when(categoryMapper.toCategoryEntity(request)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
        when(fileConverterService.convert(multipartFile)).thenReturn("convertedFileUrl");

        CategoryEntity result = categoryService.createCategory(request, multipartFile);

        assertNotNull(result);
        assertEquals(request.name(), result.getName());
        assertEquals(request.description(), result.getDescription());
        assertNull(result.getParentCategory());
        assertEquals("convertedFileUrl", result.getIconUrl());
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    public void createCategory_ShouldSetParentCategory() throws IOException {
        Long parentCategoryId = 1L;
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .parentCategoryId(parentCategoryId)
                .build();

        CategoryEntity categoryEntity = new CategoryEntity();
        CategoryEntity parentCategory = new CategoryEntity();
        parentCategory.setId(parentCategoryId);

        when(categoryMapper.toCategoryEntity(request)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
        when(fileConverterService.convert(multipartFile)).thenReturn("convertedFileUrl");
        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));

        CategoryEntity result = categoryService.createCategory(request, multipartFile);

        assertNotNull(result);
        assertEquals(parentCategory, result.getParentCategory());
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    public void createCategory_ShouldThrowExceptionIfParentNotFound() {
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("TestName")
                .description("TestDescription")
                .parentCategoryId(1L)
                .build();

        when(categoryMapper.toCategoryEntity(request)).thenReturn(new CategoryEntity());
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.createCategory(request, multipartFile));

        assertEquals("Category not found", exception.getMessage());

    }

    @Test
    void updateCategory_ShouldUpdateCategoryWithIcon() throws IOException {
        Long categoryId = 1L;
        Long parentCategoryId = 2L;

        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequest.builder()
                .name("TestUpdateName")
                .description("TestUpdateDescription")
                .parentCategoryId(parentCategoryId)
                .build();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("OldName");
        existingCategory.setDescription("OldDescription");

        CategoryEntity parentCategory = new CategoryEntity();
        parentCategory.setId(parentCategoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileConverterService.convert(multipartFile)).thenReturn("new-icon-url");
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        doAnswer(invocation -> {
            CategoryEntity category = invocation.getArgument(0);
            category.setName("TestUpdateName");
            category.setDescription("TestUpdateDescription");
            category.setParentCategory(parentCategory);
            return category;
        }).when(categoryMapper).updateCategoryEntity(any(CategoryEntity.class), any(CategoryUpdateRequest.class));

        CategoryEntity result = categoryService.updateCategory(categoryId, categoryUpdateRequest, multipartFile);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(parentCategoryId);
        verify(fileConverterService).convert(multipartFile);
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).updateCategoryEntity(existingCategory, categoryUpdateRequest);


        assertEquals("new-icon-url", result.getIconUrl());
        assertEquals(parentCategory, result.getParentCategory());
        assertEquals("TestUpdateName", result.getName());
        assertEquals("TestUpdateDescription", result.getDescription());
    }

    @Test
    void updateCategory_ShouldUpdateCategoryWithoutIcon() throws IOException {
        Long categoryId = 1L;

        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequest.builder()
                .name("TestUpdateName")
                .description("TestUpdateDescription")
                .parentCategoryId(null)
                .iconFile(null)
                .build();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("OldName");
        existingCategory.setDescription("OldDescription");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(multipartFile.isEmpty()).thenReturn(true);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        doAnswer(invocation -> {
            CategoryEntity category = invocation.getArgument(0);
            category.setName("TestUpdateName");
            category.setDescription("TestUpdateDescription");
            return category;
        }).when(categoryMapper).updateCategoryEntity(any(CategoryEntity.class), any(CategoryUpdateRequest.class));

        CategoryEntity result = categoryService.updateCategory(categoryId, categoryUpdateRequest, multipartFile);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(result);
        verify(categoryMapper).updateCategoryEntity(existingCategory, categoryUpdateRequest);

        assertNull(result.getParentCategory());
        assertNull(result.getIconUrl());
        assertEquals("TestUpdateName", result.getName());
        assertEquals("TestUpdateDescription", result.getDescription());
    }

    @Test
    void updateCategory_WithInvalidFileFormat_ShouldThrowException() throws IOException {
        Long categoryId = 1L;

        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("TestCategory")
                .description("TestDescription")
                .iconFile(multipartFile)
                .build();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("OldName");
        existingCategory.setDescription("OldDescription");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileConverterService.convert(multipartFile)).thenThrow(new InvalidFileFormatException("File is not a valid image"));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        assertThrows(InvalidFileFormatException.class, () ->
                categoryService.updateCategory(categoryId, request,multipartFile));
        assertNull(existingCategory.getIconUrl());
    }

    @Test
    void updateCategory_WithCorruptedFile_ShouldThrowIOException() throws IOException {
        Long categoryId = 1L;

        CategoryUpdateRequest categoryUpdateRequest = CategoryUpdateRequest.builder()
                .name("TestCategory")
                .description("TestDescription")
                .iconFile(multipartFile)
                .build();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("OldName");
        existingCategory.setDescription("OldDescription");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileConverterService.convert(multipartFile)).thenThrow(new IOException("File is corrupted"));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        assertThrows(IOException.class, () ->
                categoryService.updateCategory(categoryId, categoryUpdateRequest, multipartFile));

        assertNull(existingCategory.getIconUrl());
    }

    @Test
    void updateCategoryWithCyclicDependency() {
        CategoryEntity categoryA = new CategoryEntity();
        categoryA.setId(1L);

        CategoryEntity categoryB = new CategoryEntity();
        categoryB.setId(2L);
        categoryB.setParentCategory(categoryA);

        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .parentCategoryId(2L)
                .build();

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(1L, request, null));
    }

    @Test
    void updateCategoryWithEmptyRequest() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .build();

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(1L, request, null));
    }

    @Test
    void updateCategoryWithNonExistentParentCategory() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .parentCategoryId(999L)
                .build();

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(999L, request, null));
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .parentCategoryId(null)
                .iconFile(null)
                        .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(1L, request, null));
    }

    @Test
    void testDeleteCategory_Success() {
        boolean forceDelete = true;
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setName("Updated Name");
        existingCategory.setDescription("Updated Description");

        when(categoryRepository.findAllByParentCategory_Id(1L)).thenReturn(List.of());

        categoryService.deleteCategory(1L, forceDelete);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteCategory_WithChildCategories() {
        boolean forceDelete = false;
        
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setName("Updated Name");
        existingCategory.setDescription("Updated Description");

        CategoryEntity childCategory = new CategoryEntity();
        childCategory.setId(2L);
        
        when(categoryRepository.findAllByParentCategory_Id(1L)).thenReturn(List.of(childCategory));

        assertThrows(CategoryDeletionException.class, () ->
                categoryService.deleteCategory(1L, forceDelete));
        
        verify(categoryRepository, never()).deleteById(1L);
    }

}