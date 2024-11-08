package com.khutircraftubackend.category;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
	
	@Column (name = "creation_date")
	private LocalDateTime creationDate;
	
	@PrePersist
	protected void onCreate() {
		creationDate = LocalDateTime.now();
	}
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private CategoryEntity parentCategory;
}
