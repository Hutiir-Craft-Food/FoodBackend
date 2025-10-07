package com.khutircraftubackend.category.catalog;

import com.khutircraftubackend.category.CategoryEntity;
import com.khutircraftubackend.category.CategoryRepository;
import com.khutircraftubackend.category.catalog.response.CategoryTreeNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // TODO: what causes unnecessary stubbing warnings ?
class CatalogServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    @DisplayName("Should return full catalog")
    void shouldReturnEmpty_WhenCategoryMissing() {

        // Arrange each time for all:
        CategoryEntity entity1 = CategoryEntity.builder()
                .id(1L)
                .name("Напої")
                .parentCategory(null)
                .build();

            CategoryEntity entity11 = CategoryEntity.builder()
                    .id(11L)
                    .name("Алкогольні")
                    .parentCategory(entity1)
                    .build();

                CategoryEntity entity111 = CategoryEntity.builder()
                        .id(111L)
                        .name("Вина")
                        .parentCategory(entity11)
                        .build();

            CategoryEntity entity12 = CategoryEntity.builder()
                    .id(11L)
                    .name("Безалкогольні")
                    .parentCategory(entity1)
                    .build();

        CategoryEntity entity2 = CategoryEntity.builder()
                .id(2L)
                .name("Мʼясні вироби")
                .parentCategory(null)
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(entity1, entity11, entity111, entity12, entity2));
        when(categoryRepository.findAllByParentCategoryIsNull()).thenReturn(List.of(entity1, entity2));
        when(categoryRepository.findAllByParentCategory_Id(1L)).thenReturn(List.of(entity11, entity12));
        when(categoryRepository.findAllByParentCategory_Id(11L)).thenReturn(List.of(entity111));

        //Act
        List<CategoryTreeNode> result = catalogService.getCatalogTree();

        //Assert
        assertEquals(2, result.size());

        // TODO: other assertions here....
    }
}
