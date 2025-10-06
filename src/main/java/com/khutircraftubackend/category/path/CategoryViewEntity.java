package com.khutircraftubackend.category.path;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Immutable
@NoArgsConstructor
@Getter
@Subselect("select *, build_json_tree(id) as json_tree from v_categories")
@Synchronize({"categories"})
public class CategoryViewEntity {
    
    @Id
    private Long id;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    private String name;
    private String path;

    @Column(name = "json_tree")
    private String jsonTree;

    @Column(name = "icon_url")
    private String iconUrl;
    
    private String keywords;
}
