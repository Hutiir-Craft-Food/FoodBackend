package com.khutircraftubackend.category.breadcramb;

import com.khutircraftubackend.category.path.CategoryViewEntity;
import com.khutircraftubackend.category.path.CategoryViewRepository;
import com.khutircraftubackend.category.path.PathService;
import com.khutircraftubackend.category.path.response.CategoryPathItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private CategoryViewRepository repo;

    @InjectMocks
    private PathService pathService;

    @Test
    @DisplayName("Should return full breadcrumbs tree when category exists")
    void shouldReturnBreadcrumbs_WhenCategoryExists() {

        //Arrange
        CategoryViewEntity entity = mock(CategoryViewEntity.class);
        when(entity.getPathIds()).thenReturn("1,2,3");
        when(entity.getPathNames()).thenReturn("Напої, Алкогольні, Вино");

        when(repo.findById(3L)).thenReturn(Optional.of(entity));

        //Act
        CategoryPathItem root = pathService.getCategoryPathItem(3L);

        //Assert
        assertEquals("Напої", root.name());
        assertEquals(1, root.children().size());
    
        CategoryPathItem alk = root.children().get(0);
        assertEquals("Алкогольні", alk.name());
    
        CategoryPathItem vino = alk.children().get(0);
        assertEquals("Вино", vino.name());
    }

    @Test
    @DisplayName("Should return empty result when category is missing")
    void shouldReturnEmpty_WhenCategoryMissing() {

        //Arrange
        when(repo.findById(99L)).thenReturn(Optional.empty());

        //Act
        CategoryPathItem result = pathService.getCategoryPathItem(99L);

        //Assert
        assertNull(result);
    }
}
