package com.khutircraftubackend.seller.qualityCertificates;

import com.khutircraftubackend.Auditable;
import com.khutircraftubackend.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quality_certificates")
public class QualityCertificateEntity extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "certificate_url")
	private String certificateUrl;
	
	@Column(name = "issue_date", nullable = false)
	private LocalDateTime issue_date;
	
	@Column(name = "expiration_date", nullable = false)
	private LocalDateTime expiration_date;
	
	@ManyToOne
	@JoinColumn(name = "seller_id")
	private SellerEntity seller;
}
