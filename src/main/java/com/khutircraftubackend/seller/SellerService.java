package com.khutircraftubackend.seller;

import com.khutircraftubackend.address.AddressEntity;
import com.khutircraftubackend.address.AddressMapper;
import com.khutircraftubackend.address.AddressRequest;
import com.khutircraftubackend.address.AddressService;
import com.khutircraftubackend.address.exception.address.AccessDeniedException;
import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.seller.exception.seller.SellerNotFoundException;
import com.khutircraftubackend.seller.qualityCertificates.*;
import com.khutircraftubackend.seller.request.SellerRequest;
import com.khutircraftubackend.seller.response.SellerResponse;
import com.khutircraftubackend.storage.StorageService;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Клас SellerService реалізує бізнес-логіку для роботи з продавцями.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {
	
	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final UserService userService;
	private final QualityCertificateService qualityCertificateService;
	private final AddressService addressService;
	private final SellerMapper sellerMapper;
	private final StorageService storageService;
	private final QualityCertificateMapper qualityCertificateMapper;
	private final AddressMapper addressMapper;
	
	public SellerEntity getByUser(UserEntity user) {
		
		return sellerRepository.findByUser(user)
				.orElseThrow(() -> new SellerNotFoundException("User is not a valid Seller"));
	}
	
	public SellerEntity getSellerId(Long id) {
		
		return sellerRepository.findById(id)
				.orElseThrow(() -> new SellerNotFoundException("Seller not found"));
	}
	
	private String handleImage(MultipartFile iconFile) throws IOException, URISyntaxException {
		
		if (iconFile != null && !iconFile.isEmpty()) {
			
			return storageService.upload(iconFile);
		}
		
		return null;
	}
	
	public void createSeller(RegisterRequest request, UserEntity user) {
		SellerEntity seller = SellerEntity
				.builder()
				.sellerName(request.details().sellerName())
				.companyName(request.details().companyName())
				.phoneNumber(request.details().phoneNumber())
				.customerPhoneNumber(request.details().customerPhoneNumber())
				.description(request.details().description())
				.address(addressMapper.toAddressEntity(request.details().addressRequest()))
				.user(user)
				.build();
		sellerRepository.save(seller);
	}
	
	
	public SellerResponse getSellerInfo(Principal principal) {
		UserEntity user = userService.findByPrincipal(principal);
		SellerEntity seller = getByUser(user);
		
		return sellerMapper.toSellerResponse(seller);
	}
	
	public SellerEntity getCurrentSeller() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
		UserEntity currentUser = userRepository.findByEmail(currentUserDetails.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User is not found"));
		
		return getByUser(currentUser);
	}
	
	private void validateAddressOwnership(AddressEntity addressEntity, SellerEntity sellerEntity) {
		
		if (!Objects.equals(addressEntity.getId(), sellerEntity.getAddress().getId())) {
			throw new AccessDeniedException("Access denied: Address does not belong to the seller");
		}
	}
	
	private void validateCertificateOwnership(QualityCertificateEntity certificateEntity, SellerEntity sellerEntity) {
		
		if (!certificateEntity.getSeller().equals(sellerEntity)) {
			throw new AccessDeniedException("Access denied: Certificate does not belong to the seller");
			
		}
	}
	
	@Transactional
	public QualityCertificateResponse createSellerCertificates(QualityCertificateRequest request, MultipartFile certificateFile) throws IOException, URISyntaxException {
		
		SellerEntity seller = getCurrentSeller();
		
		QualityCertificateEntity certificate = qualityCertificateService.createQualityCertificate(request, certificateFile);
		
		seller.addCertificate(certificate);
		
		sellerRepository.save(seller);
		
		return qualityCertificateMapper.toCertificateResponse(certificate);
	}
	
	@Transactional
	public QualityCertificateResponse updateSellerCertificate(Long id, QualityCertificateRequest request, MultipartFile certificateFile) throws IOException, URISyntaxException {
		
		SellerEntity seller = getCurrentSeller();
		
		QualityCertificateEntity certificate = qualityCertificateService.findQualityCertificateBiId(id, seller);
		
		validateCertificateOwnership(certificate, seller);
		
		qualityCertificateService.updateQualityCertificate(id, request, certificateFile);
		
		seller.addCertificate(certificate);
		
		return qualityCertificateMapper.toCertificateResponse(certificate);
	}
	
	
	@Transactional
	public SellerResponse updateSellerInfoWithAddress(SellerRequest request,
													  Long sellerId,
													  Long addressId,
													  AddressRequest addressRequest,
													  MultipartFile logoFile
	) throws IOException, URISyntaxException {
		
		SellerEntity seller = getSellerId(sellerId);
		
		sellerMapper.updateSellerFromRequest(seller, request);
		
		AddressEntity address = addressService.findAddressById(addressId);
		validateAddressOwnership(address, seller);
		
		AddressEntity updateAddress = addressService.updateAddress(addressId, addressRequest);
		seller.setAddress(updateAddress);
		
		seller.setLogoUrl(handleImage(logoFile));
		
		return sellerMapper.toSellerResponse(sellerRepository.save(seller));
		
	}
	
	@Transactional
	public void deleteSeller(Long sellerId) {
		
		SellerEntity seller = getSellerId(sellerId);
		
		sellerRepository.delete(seller);
	}
	
	@Transactional
	public void deleteCertificate(Long certificateId) {
		
		SellerEntity seller = getCurrentSeller();
		
		QualityCertificateEntity certificate = qualityCertificateService.findQualityCertificateBiId(certificateId, seller);
		
		validateCertificateOwnership(certificate, seller);
		
		seller.removeCertificate(certificate);
		
		qualityCertificateService.deleteQualityCertificate(certificateId);
	}
	
	@Transactional(readOnly = true)
	public List<QualityCertificateResponse> getSellerCertificates() {
		
		SellerEntity seller = getCurrentSeller();
		
		return seller.getQualityCertificatesUrl().stream()
				.map(qualityCertificateMapper::toCertificateResponse)
				.collect(Collectors.toList());
	}
	
}
