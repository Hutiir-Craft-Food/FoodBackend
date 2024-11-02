package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.category.CategoryDeletionException;
import com.khutircraftubackend.category.exception.category.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.product.image.FileConverterService;
import com.khutircraftubackend.product.image.FileUploadService;
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
    private MultipartFile multipartFile;
    @Mock
    private FileConverterService fileConverterService;
    @Mock
    private FileUploadService fileUploadService;
    
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
        CategoryRequest request = CategoryRequest.builder()
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
        CategoryRequest request = CategoryRequest.builder()
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
        CategoryRequest request = CategoryRequest.builder()
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
    void testUpdateCategoryWithoutIcon() throws IOException {
        
        Long categoryId = 1L;
        
        CategoryRequest request = CategoryRequest.builder()
                .name("New category")
                .description("Updated description")
                .parentCategoryId(null)
                .build();
        
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("Old name");
        existingCategory.setDescription("Old description");
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
    
        doAnswer(invocation -> {
            CategoryEntity entity = invocation.getArgument(0);
            entity.setName(request.name());
            entity.setDescription(request.description());
            return entity;
        }).when(categoryMapper).updateCategoryEntity(any(CategoryEntity.class), any(CategoryRequest.class));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
    
        CategoryEntity updateCategory = categoryService.updateCategory(categoryId, request, multipartFile);
    
        assertEquals(request.name(), updateCategory.getName());
        assertEquals(request.description(), updateCategory.getDescription());
        assertNull(updateCategory.getIconUrl());
        assertNull(updateCategory.getParentCategory());
    
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).updateCategoryEntity(existingCategory, request);
    }
    
    @Test
    void testUpdateCategoryWithIcon() throws IOException {
        
        Long categoryId = 1L;
        Long parentCategoryId = 2L;
    
        CategoryRequest request = CategoryRequest.builder()
                .name("New Category")
                .description("Updated Description")
                .parentCategoryId(parentCategoryId)
                .build();
        
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("Old Name");
        existingCategory.setDescription("Old Description");
        existingCategory.setIconUrl("http://example.com/icon.png");
    
        CategoryEntity parentCategory = new CategoryEntity();
        parentCategory.setId(parentCategoryId);
        
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileConverterService.convert(multipartFile)).thenReturn("newIconUrl");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
    
        doAnswer(invocation -> {
            CategoryEntity entity = invocation.getArgument(0);
            entity.setName(request.name());
            entity.setDescription(request.description());
            entity.setParentCategory(new CategoryEntity());
            entity.getParentCategory().setId(request.parentCategoryId());
            return null;
        }).when(categoryMapper).updateCategoryEntity(existingCategory, request);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
    
        CategoryEntity updatedCategory = categoryService.updateCategory(categoryId, request, multipartFile);
    
        assertEquals(request.name(), updatedCategory.getName());
        assertEquals(request.description(), updatedCategory.getDescription());
        assertNotNull(updatedCategory.getParentCategory());
        assertEquals(request.parentCategoryId(), updatedCategory.getParentCategory().getId());
        assertEquals("newIconUrl", updatedCategory.getIconUrl());
    
        verify(fileUploadService).deleteCloudinaryById(fileUploadService.extractPublicId("http://example.com/icon.png"));
        verify(fileConverterService).convert(multipartFile);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(parentCategoryId);
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).updateCategoryEntity(existingCategory, request);
    }
    
    
    @Test
    void testUpdateCategory_WithInvalidFileFormat_ShouldThrowException() throws IOException {
        Long categoryId = 1L;
        
        CategoryRequest request = CategoryRequest.builder()
                .name("TestCategory")
                .description("TestDescription")
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
    void testUpdateCategory_WithCorruptedFile_ShouldThrowIOException() throws IOException {
        Long categoryId = 1L;
        
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("TestCategory")
                .description("TestDescription")
                .build();
        
        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("OldName");
        existingCategory.setDescription("OldDescription");
        
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileConverterService.convert(multipartFile)).thenThrow(new IOException("File is corrupted"));
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        
        assertThrows(IOException.class, () ->
                categoryService.updateCategory(categoryId, categoryRequest, multipartFile));
        
        assertNull(existingCategory.getIconUrl());
    }
    
    @Test
    void testUpdateCategoryWithCyclicDependency() {
        CategoryEntity categoryA = new CategoryEntity();
        categoryA.setId(1L);
        
        CategoryEntity categoryB = new CategoryEntity();
        categoryB.setId(2L);
        categoryB.setParentCategory(categoryA);
        
        CategoryRequest request = CategoryRequest.builder()
                .parentCategoryId(2L)
                .build();
        
        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(1L, request, null));
    }
    
    @Test
    void testUpdateCategoryWithEmptyRequest() {
        CategoryRequest request = CategoryRequest.builder()
                .build();
        
        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(1L, request, null));
    }
    
    @Test
    void testUpdateCategoryWithNonExistentParentCategory() {
        CategoryRequest request = CategoryRequest.builder()
                .parentCategoryId(999L)
                .build();
        
        assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(999L, request, null));
    }
    
    @Test
    void testUpdateCategory_NotFound() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .parentCategoryId(null)
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