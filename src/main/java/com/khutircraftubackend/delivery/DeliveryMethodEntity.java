package com.khutircraftubackend.delivery;

import com.khutircraftubackend.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_methods")
public class DeliveryMethodEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "cost", nullable = false)
	private BigDecimal cost;
	
	@Column(name = "estimated_delivery_time", nullable = false)//TODO necessary changed type to LocalData for this field
	private String estimatedDeliveryTime;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "delivery_provider", nullable = false)
	private DeliveryMethodProvider deliveryProvider;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private SellerEntity seller;
	
}
