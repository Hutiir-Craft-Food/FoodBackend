package com.khutircraftubackend.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "categories")
public class CategoryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "icon_url")
	private String iconUrl;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private CategoryEntity parentCategory;
	
	@Column(name = "keywords")
	@JsonIgnore
	private String keywords;
}
