package com.khutircraftubackend.category.breadcramb;

import com.khutircraftubackend.category.path.CategoryPathMapper;
import com.khutircraftubackend.category.path.PathService;
import com.khutircraftubackend.category.path.CategoryViewEntity;
import com.khutircraftubackend.category.path.CategoryViewRepository;
import com.khutircraftubackend.category.path.response.CategoryPathItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {
    
    @Mock
    private CategoryViewRepository repo;
    
    @Spy
    private CategoryPathMapper mapper = Mappers.getMapper(CategoryPathMapper.class);
    
    @InjectMocks
    private PathService pathService;
    
    @Test
    @DisplayName("Should return full breadcrumbs when category exists")
    void shouldReturnBreadcrumbs_WhenCategoryExists() {
        
        //Arrange
        CategoryViewEntity entity = mock(CategoryViewEntity.class);
        when(entity.getPathIds()).thenReturn("1,2,3");
        when(entity.getPathNames()).thenReturn("Напої, Алкогольні, Вино");
        
        when(repo.findById(3L)).thenReturn(Optional.of(entity));
        
        //Act
        List<CategoryPathItem> breadcrumbs = pathService.getCategoryPathItem(3L);
        
        //Assert
        assertEquals(4, breadcrumbs.size());
        assertEquals("Головна", breadcrumbs.get(0).name());
        assertEquals("Вино", breadcrumbs.get(3).name());
    }
    
    @Test
    @DisplayName("Should return only root when category is missing")
    void shouldReturnOnlyRoot_WhenCategoryMissing() {
        
        //Arrange
        when(repo.findById(99L)).thenReturn(Optional.empty());
        
        //Act
        List<CategoryPathItem> breadcrumbs = pathService.getCategoryPathItem(99L);
        
        //Assert
        assertEquals(1, breadcrumbs.size());
        assertEquals("Головна", breadcrumbs.get(0).name());
    }
}
