package com.khutircraftubackend.seller.qualityCertificates;

import com.khutircraftubackend.seller.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QualityCertificateRepository extends JpaRepository<QualityCertificateEntity, Long> {
	
	List<QualityCertificateEntity> findAllBySeller(SellerEntity seller);
	Optional<QualityCertificateEntity> findByIdAndSeller(Long id, SellerEntity seller);
}
