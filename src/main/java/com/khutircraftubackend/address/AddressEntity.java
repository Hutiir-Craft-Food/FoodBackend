package com.khutircraftubackend.address;

import com.khutircraftubackend.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class AddressEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "country", nullable = false)
	private String country;
	
	@Column(name = "city", nullable = false)
	private String city;
	
	@Column(name = "street", nullable = false)
	private String street;
	
	@Column(name = "house_number")
	private String houseNumber;
	
	@Column(name = "apartment_number")
	private String apartmentNumber;
	
	@Column(name = "postal_code", nullable = false)
	private String postalCode;
	
	@Column (name = "creation_date")
	private LocalDateTime creationDate;
	
	@OneToOne(mappedBy = "address", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private SellerEntity seller;
	
	@PrePersist
	protected void onCreate() {
		creationDate = LocalDateTime.now();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AddressEntity that = (AddressEntity) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(country, that.country) &&
				Objects.equals(city, that.city) &&
				Objects.equals(street, that.street) &&
				Objects.equals(houseNumber, that.houseNumber) &&
				Objects.equals(apartmentNumber, that.apartmentNumber) &&
				Objects.equals(postalCode, that.postalCode);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, country, city, street, houseNumber, apartmentNumber, postalCode);
	}
}
