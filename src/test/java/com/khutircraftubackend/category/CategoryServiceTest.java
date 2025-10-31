package com.khutircraftubackend.category;

import com.khutircraftubackend.category.exception.CategoryDeletionException;
import com.khutircraftubackend.category.exception.CategoryNotFoundException;
import com.khutircraftubackend.category.request.CategoryRequest;
import com.khutircraftubackend.product.image.exception.ImageProcessingException;
import com.khutircraftubackend.search.exception.SearchResponseMessage;
import com.khutircraftubackend.search.exception.InvalidSearchQueryException;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.storage.exception.InvalidFileFormatException;
import com.khutircraftubackend.storage.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private MultipartFile multipartFile;

	@Mock
	private StorageService storageService;

	@Spy
	private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

	@InjectMocks
	private CategoryService categoryService;

	private byte [] bytes;

	@BeforeEach
	void setup() {
		try {
			bytes = multipartFile.getBytes();
		} catch (IOException e) {
			throw new ImageProcessingException(e.getMessage());
		}
	}

	@Nested
	@DisplayName("Tests for all Category")
	class AllCategory {

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
		
	}
	
	@Nested
	@DisplayName("Tests for Category creation")
	class CreateCategory {

		@Test
		void createCategory_ShouldUploadFile(){
			
			CategoryRequest request = CategoryRequest.builder()
					.name("TestName")
					.description("TestDescription")
					.parentCategoryId(null)
					.build();
			
			CategoryEntity categoryEntity = new CategoryEntity();
			categoryEntity.setName(request.name());
			categoryEntity.setDescription(request.description());
			categoryEntity.setIconUrl("cloudinaryUrl");

			when(storageService.upload(bytes, null)).thenReturn("cloudinaryUrl");
			when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);
			
			CategoryEntity result = categoryService.createCategory(request, multipartFile);
			
			assertNotNull(result);
			assertEquals("cloudinaryUrl", result.getIconUrl());
			verify(storageService).upload(bytes, null);
			verify(categoryRepository, times(2)).save(any(CategoryEntity.class));
		}
		
		@Test
		void createCategory_ShouldSetParentCategory() {
			Long parentCategoryId = 1L;
			CategoryRequest request = CategoryRequest.builder()
					.name("TestName")
					.description("TestDescription")
					.parentCategoryId(parentCategoryId)
					.build();
			
			CategoryEntity parentCategory = new CategoryEntity();
			parentCategory.setId(parentCategoryId);
			
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setParentCategory(parentCategory);
			categoryEntity.setIconUrl("convertedFileUrl");
            
			when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);
			when(storageService.upload(bytes, null)).thenReturn("convertedFileUrl");
			when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
			
			CategoryEntity result = categoryService.createCategory(request, multipartFile);
			
			assertNotNull(result);
			assertEquals(parentCategory, result.getParentCategory());
			assertEquals("convertedFileUrl", result.getIconUrl());
			
			verify(storageService).upload(bytes, null);
			verify(categoryRepository, times(2)).save(any(CategoryEntity.class));
		}
		
		@Test
		void createCategory_ShouldThrowExceptionIfParentNotFound() {
			CategoryRequest request = CategoryRequest.builder()
					.name("TestName")
					.description("TestDescription")
					.parentCategoryId(1L)
					.build();
			
			when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
			
			Exception exception = assertThrows(CategoryNotFoundException.class, () ->
					categoryService.createCategory(request, multipartFile));
			
			assertEquals("Категорія не знайдена", exception.getMessage());
			
		}
		
	}
	
	@Nested
	@DisplayName("Tests for Category update")
	class UpdateCategory {

		@Test
		void updateCategory_ShouldUpdateIconWithCloudinary(){
			Long categoryId = 1L;
			CategoryRequest request = CategoryRequest.builder()
					.name("UpdatedName")
					.description("UpdatedDescription")
					.build();
			
			CategoryEntity existingCategory = new CategoryEntity();
			existingCategory.setId(categoryId);
			existingCategory.setIconUrl("oldCloudinaryUrl");
			
			when(multipartFile.isEmpty()).thenReturn(false);
			when(storageService.upload(bytes, null)).thenReturn("newCloudinaryUrl");
			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
			when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
			
			CategoryEntity updatedCategory = categoryService.updateCategory(categoryId, request, multipartFile);
			
			assertEquals("newCloudinaryUrl", updatedCategory.getIconUrl());
			verify(storageService).upload(bytes, null);
			verify(categoryRepository).save(existingCategory);
		}
		
		
		@Test
		void updateCategory_ShouldUpdateIconWithLocalStorage(){
			Long categoryId = 1L;
			CategoryRequest request = CategoryRequest.builder()
					.name("UpdatedName")
					.description("UpdatedDescription")
					.build();

			CategoryEntity existingCategory = new CategoryEntity();
			existingCategory.setId(categoryId);
			existingCategory.setIconUrl("oldLocalPath");

			when(storageService.upload(any(), isNull())).thenReturn("newLocalPath");
			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
			when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(existingCategory);

			CategoryEntity updatedCategory = categoryService.updateCategory(categoryId, request, multipartFile);

			assertEquals("newLocalPath", updatedCategory.getIconUrl());
			verify(storageService).upload(any(), isNull());
			verify(categoryRepository).save(any(CategoryEntity.class));
		}
		
		@Test
		void updateCategory_WithInvalidFileFormat_ShouldThrowException() {
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
			when(storageService.upload(bytes, null)).thenThrow(new InvalidFileFormatException("File is not a valid image"));
			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
			
			assertThrows(InvalidFileFormatException.class, () ->
					categoryService.updateCategory(categoryId, request, multipartFile));
			assertNull(existingCategory.getIconUrl());
		}
		
		@Test
		void updateCategory_WithCorruptedFile_ShouldThrowCloudStorageException() {
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
			when(storageService.upload(bytes, null)).thenThrow(new StorageException("File is corrupted"));
			
			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
			
			assertThrows(StorageException.class, () ->
					categoryService.updateCategory(categoryId, categoryRequest, multipartFile));
			
			assertNull(existingCategory.getIconUrl());
		}
		
		@Test
		void updateCategoryWithCyclicDependency() {
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
		void updateCategoryWithEmptyRequest() {
			CategoryRequest request = CategoryRequest.builder()
					.build();
			
			assertThrows(CategoryNotFoundException.class, () ->
					categoryService.updateCategory(1L, request, null));
		}
		
		@Test
		void updateCategoryWithNonExistentParentCategory() {
			CategoryRequest request = CategoryRequest.builder()
					.parentCategoryId(999L)
					.build();
			
			assertThrows(CategoryNotFoundException.class, () ->
					categoryService.updateCategory(999L, request, null));
		}
		
		@Test
		void updateCategory_NotFound() {
			CategoryRequest request = CategoryRequest.builder()
					.name("Updated Name")
					.description("Updated Description")
					.parentCategoryId(null)
					.build();
			
			when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
			
			assertThrows(CategoryNotFoundException.class, () ->
					categoryService.updateCategory(1L, request, null));
		}
		
	}
	
	@Nested
	@DisplayName("Tests for Category delete")
	class DeleteCategory {

		@Test
		void deleteCategory_Success() {
			boolean forceDelete = true;
			CategoryEntity existingCategory = new CategoryEntity();
			existingCategory.setId(1L);
			existingCategory.setName("Updated Name");
			existingCategory.setDescription("Updated Description");
			
			when(categoryRepository.findAllByParentCategory_Id(1L)).thenReturn(List.of());
			when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
			
			categoryService.deleteCategory(1L, forceDelete);
			
			verify(categoryRepository).deleteById(1L);
		}
		
		@Test
		void deleteCategory_WithChildCategories() {
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

	@Nested()
	@DisplayName("Keyword Operations")
	class Keywords {

		private CategoryEntity category;

		@BeforeEach
		void setUp() {
			category = new CategoryEntity();
			category.setId(8L);
			category.setKeywords("вода,напій");
			lenient().when(categoryRepository.findById(8L)).thenReturn(Optional.of(category));
		}

		@Test
		void shouldAddNewKeywordsInTheSameOrder () {
			Set<String> newKeywords = new LinkedHashSet<>();
			newKeywords.add("сік");
			newKeywords.add("вода");
			newKeywords.add("чай");

			categoryService.updateKeywords(8L, newKeywords);

			assertEquals("сік,вода,чай", category.getKeywords());
			verify(categoryRepository).save(category);
		}

		@Test
		void shouldHandleEmptyKeywords() {
			category.setKeywords(null);

			categoryService.updateKeywords(8L, Collections.emptySet());

			assertNull(category.getKeywords());
			verify(categoryRepository).save(category);
		}

		@Test
		void shouldIgnoreRedundantWhiteSpaces() {
			Set<String> newKeywords = new LinkedHashSet<>();
			newKeywords.add("  лимон   ");
			newKeywords.add("апельсин");
			newKeywords.add("   банан  ");

			categoryService.updateKeywords(8L, newKeywords);

			assertEquals("лимон,апельсин,банан", category.getKeywords());
			verify(categoryRepository).save(category);
		}

		@Test
		void shouldThrowExceptionWhenKeywordsAreNull() {
			// TODO: should we test it at CategoryMapper level instead ?
			Set<String> invalidKeywords = Set.of("", "   ", "\t", "\n");

			assertThatThrownBy(() -> categoryService.updateKeywords(8L, invalidKeywords))
					.isInstanceOf(InvalidSearchQueryException.class)
					.hasMessage(SearchResponseMessage.EMPTY_KEYWORDS_ERROR);
		}
	}
}