package com.khutircraftubackend.category;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "categories")
public class CategoryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@Column(name = "name", nullable = false, unique = true)
	String name;
	
	@Column(name = "description")
	String description;
	
	@Column(name = "icon_url")
	String iconUrl;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	CategoryEntity parentCategory;
}
