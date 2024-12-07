package com.khutircraftubackend.category;

import com.khutircraftubackend.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "categories")
public class CategoryEntity extends Auditable {
	
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
}
