package com.khutircraftubackend;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Auditable {
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	@Column(name = "create_date", updatable = false, nullable = false)
	private LocalDateTime createDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	@Column(name = "update_date")
	private LocalDateTime updateDate;
	
	
	@PrePersist
	protected void onCreate() {
		createDate = LocalDateTime.now();
		updateDate = LocalDateTime.now();
	}
	
	
	@PreUpdate
	protected void onUpdate() {
		updateDate = LocalDateTime.now();
	}
	
}
