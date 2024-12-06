package com.khutircraftubackend.seller.qualityCertificates;

import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.qualityCertificates.exception.qualityException.InvalidQualityCertificateArgumentException;
import com.khutircraftubackend.seller.qualityCertificates.exception.qualityException.QualityCertificateNotFoundException;
import com.khutircraftubackend.storage.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QualityCertificateService {
	
	private final QualityCertificateRepository qualityCertificateRepository;
	private final QualityCertificateMapper qualityCertificateMapper;
	private final StorageService storageService;
	
	public QualityCertificateEntity findQualityCertificateBiId(Long id, SellerEntity seller) {
		
		return qualityCertificateRepository.findByIdAndSeller(id, seller)
				.orElseThrow(() -> new QualityCertificateNotFoundException("Quality Certificate not found or does not belong to the seller"));
	}
	
	private String handleImage(MultipartFile certificateFile) throws IOException, URISyntaxException {
		
		if (certificateFile != null && !certificateFile.isEmpty()) {
			return storageService.upload(certificateFile);
		}
		
		return null;
	}
	
	private void validateDates(LocalDateTime issueDate, LocalDateTime expirationDate) {
		
		if (expirationDate.isBefore(issueDate)) {
			throw new InvalidQualityCertificateArgumentException("Expiration date must be after the issue date");
		}
	}
	
	@Transactional
	public QualityCertificateEntity createQualityCertificate(QualityCertificateRequest request, MultipartFile certificateFile) throws IOException, URISyntaxException {
	
		QualityCertificateEntity certificate = qualityCertificateMapper.toCertificateEntity(request);
		
		validateDates(request.issue_date(), request.expiration_date());
		
		certificate.setCertificateUrl(handleImage(certificateFile));
		
		return qualityCertificateRepository.save(certificate);
	}
	
	@Transactional
	public void updateQualityCertificate(Long id,
										 QualityCertificateRequest request,
										 MultipartFile certificateFile) throws IOException, URISyntaxException {
		
		SellerEntity seller = request.seller();
		
		QualityCertificateEntity existingEntity = findQualityCertificateBiId(id, seller);
		
		validateDates(request.issue_date(), request.expiration_date());
		
		existingEntity.setCertificateUrl(handleImage(certificateFile));
		
		qualityCertificateMapper.updateCertificateFromRequest(existingEntity, request);
		
		qualityCertificateRepository.save(existingEntity);
	}
	
	
		@Transactional
		public void deleteQualityCertificate (Long id){
			
			qualityCertificateRepository.deleteById(id);
		}
		
	}
