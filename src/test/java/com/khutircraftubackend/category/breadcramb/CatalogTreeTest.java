package com.khutircraftubackend.category.breadcramb;

import com.khutircraftubackend.category.breadcrumb.BreadcrumbMapper;
import com.khutircraftubackend.category.breadcrumb.BreadcrumbService;
import com.khutircraftubackend.category.breadcrumb.CategoryViewEntity;
import com.khutircraftubackend.category.breadcrumb.CategoryViewRepository;
import com.khutircraftubackend.category.breadcrumb.response.CatalogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogTreeTest {
    
    @Mock
    private CategoryViewRepository repo;
    
    @Spy
    private BreadcrumbMapper mapper = Mappers.getMapper(BreadcrumbMapper.class);
    
    @InjectMocks
    private BreadcrumbService service;
    
    private CategoryViewEntity rootEntity;
    
    @BeforeEach
    void setUp() {
        
        rootEntity = mock(CategoryViewEntity.class);
        when(rootEntity.getId()).thenReturn(1L);
        when(rootEntity.getName()).thenReturn("Напої");
        when(rootEntity.getParentId()).thenReturn(null);
    }
    
    @Test
    @DisplayName("Should return single root when only one category without parent")
    void shouldReturnSingleRoot_WhenOneCategory() {
        
        //Arrange
        when(repo.findAll()).thenReturn(List.of(rootEntity));
        
        //Act
        List<CatalogResponse> catalogTree = service.getCatalogTree();
        
        //Assert
        assertEquals(1, catalogTree.size());
        CatalogResponse root = catalogTree.get(0);
        assertEquals(1L, root.getId());
        assertEquals("Напої", root.getName());
        assertTrue(root.getChildren().isEmpty());
    }
    
    @Test
    @DisplayName("Should build tree with root and children")
    void shouldBuildTree_WithRootAndChildren() {
        
        //Arrange
        CategoryViewEntity childEntity = mock(CategoryViewEntity.class);
        when(childEntity.getId()).thenReturn(2L);
        when(childEntity.getName()).thenReturn("Алкогольні");
        when(childEntity.getParentId()).thenReturn(1L);
        
        when(repo.findAll()).thenReturn(List.of(rootEntity, childEntity));
        
        //Act
        List<CatalogResponse> catalogTree = service.getCatalogTree();
        
        //Assert
        assertEquals(1, catalogTree.size());
        CatalogResponse root = catalogTree.get(0);
        assertEquals("Напої", root.getName());
        assertEquals(1, root.getChildren().size());
        assertEquals("Алкогольні", root.getChildren().get(0).getName());
    }
    
    @Test
    @DisplayName("Should build tree with root and multiple children")
    void shouldBuildTree_WithRootAndMultipleChildren() {
        
        //Arrange
        CategoryViewEntity childEntity1 = mock(CategoryViewEntity.class);
        when(childEntity1.getId()).thenReturn(2L);
        when(childEntity1.getName()).thenReturn("Алкогольні");
        when(childEntity1.getParentId()).thenReturn(1L);
        
        CategoryViewEntity childEntity2 = mock(CategoryViewEntity.class);
        when(childEntity2.getId()).thenReturn(3L);
        when(childEntity2.getName()).thenReturn("Безалкогольні");
        when(childEntity2.getParentId()).thenReturn(1L);
        
        when(repo.findAll()).thenReturn(List.of(rootEntity, childEntity1, childEntity2));
        
        //Act
        List<CatalogResponse> catalogTree = service.getCatalogTree();
        
        //Assert
        CatalogResponse root = catalogTree.get(0);
        assertEquals(2, root.getChildren().size());
        List<String> childNames = root.getChildren().stream()
                .map(CatalogResponse::getName)
                .toList();
        assertTrue(childNames.contains("Алкогольні"));
        assertTrue(childNames.contains("Безалкогольні"));
    }
    
    
    @Test
    @DisplayName("Should build tree with root and deep hierarchy")
    void shouldBuildTree_WithRootAndDeepHierarchy() {
        
        //Arrange
        CategoryViewEntity childEntity1 = mock(CategoryViewEntity.class);
        when(childEntity1.getId()).thenReturn(2L);
        when(childEntity1.getName()).thenReturn("Алкогольні");
        when(childEntity1.getParentId()).thenReturn(1L);
        
        CategoryViewEntity childEntity2 = mock(CategoryViewEntity.class);
        when(childEntity2.getId()).thenReturn(3L);
        when(childEntity2.getName()).thenReturn("Вина");
        when(childEntity2.getParentId()).thenReturn(2L);
        
        when(repo.findAll()).thenReturn(List.of(rootEntity, childEntity1, childEntity2));
        
        //Act
        List<CatalogResponse> catalogTree = service.getCatalogTree();
        
        //Assert
        CatalogResponse root = catalogTree.get(0);
        assertEquals(1, root.getChildren().size());
        CatalogResponse child1 = root.getChildren().get(0);
        assertEquals("Алкогольні", child1.getName());
        
        assertEquals(1, child1.getChildren().size());
        assertEquals("Вина", child1.getChildren().get(0).getName());
    }
    
    @Test
    @DisplayName("Should return two roots when two categories roots")
    void shouldReturnTwoRoots_WhenTwoCategoriesRoots() {
        
        //Arrange
        CategoryViewEntity rootEntity2 = mock(CategoryViewEntity.class);
        when(rootEntity2.getId()).thenReturn(4L);
        when(rootEntity2.getName()).thenReturn("Мʼясні вироби");
        when(rootEntity2.getParentId()).thenReturn(null);
        
        when(repo.findAll()).thenReturn(List.of(rootEntity, rootEntity2));
        
        //Act
        List<CatalogResponse> catalogTree = service.getCatalogTree();
        
        //Assert
        assertEquals(2, catalogTree.size());
        List<String> rootNames = catalogTree.stream()
                .map(CatalogResponse::getName)
                .toList();
        assertTrue(rootNames.contains("Напої"));
        assertTrue(rootNames.contains("Мʼясні вироби"));
        
    }
    
    @Test
    @DisplayName("Should ignore orphan categories when parent is missing")
    void shouldIgnoreOrphan_WhenParentMissing() {
        
        //Arrange
        CategoryViewEntity orphanEntity = mock(CategoryViewEntity.class);
        when(orphanEntity.getId()).thenReturn(99L);
        when(orphanEntity.getName()).thenReturn("Орфан");
        when(orphanEntity.getParentId()).thenReturn(500L);
        
        when(repo.findAll()).thenReturn(List.of(rootEntity, orphanEntity));
        
        //Act
        List<CatalogResponse> catalogTree = service.getCatalogTree();
        
        //Assert
        assertEquals(1, catalogTree.size());
        CatalogResponse root = catalogTree.get(0);
        assertEquals("Напої", root.getName());
        assertTrue(root.getChildren().isEmpty());
    }
}
