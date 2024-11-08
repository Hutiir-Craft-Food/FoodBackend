package com.khutircraftubackend.seller;

import com.khutircraftubackend.address.AddressRequest;
import com.khutircraftubackend.seller.qualityCertificates.*;
import com.khutircraftubackend.seller.request.SellerRequest;
import com.khutircraftubackend.seller.response.SellerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

/**
 * Клас SellerController обробляє запити, пов'язані з продавцями.
 */

@RestController
@RequestMapping("/v1/sellers")
@RequiredArgsConstructor
public class SellerController {
	
	private final SellerService sellerService;
	
	@GetMapping("/info")
	public SellerResponse getSellerInfo(Principal principal) {
		
		return sellerService.getSellerInfo(principal);
	}
	
	
	@PutMapping(value = "/seller/{sellerId}/address/{addressId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.OK)
	public SellerResponse updateSeller(
			@Valid @ModelAttribute SellerRequest sellerRequest,
			@Valid @ModelAttribute AddressRequest addressRequest,
			@PathVariable Long sellerId,
			@PathVariable Long addressId,
			@RequestPart(value = "logo", required = false) MultipartFile logo
	) throws IOException {
		
		return sellerService.updateSellerInfoWithAddress(
				sellerRequest, sellerId, addressId, addressRequest, logo);
	}
	
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSeller(
			@PathVariable Long id
	) {
		sellerService.deleteSeller(id);
	}
	
	@PostMapping(value = "/certificate", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.CREATED)
	public QualityCertificateResponse createCertificate(
			@Valid @ModelAttribute QualityCertificateRequest request,
			@RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile
	) throws IOException {
		return sellerService.createSellerCertificates(request, certificateFile);
	}
	
	
	@PutMapping(value = "/certificate/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.OK)
	public QualityCertificateResponse updateCertificate(
			@PathVariable Long id,
			@Valid @ModelAttribute QualityCertificateRequest certificateRequest,
			@RequestPart(required = false) MultipartFile certificateFile) throws IOException {
		
		return sellerService.updateSellerCertificate(id, certificateRequest, certificateFile);
	}
	
	@DeleteMapping(value = "/certificate/{id}")
	@PreAuthorize("hasRole('SELLER')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(
            @PathVariable Long id) {
		
		sellerService.deleteCertificate(id);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
		public Collection<QualityCertificateResponse> getCertificates() {
		
		return sellerService.getSellerCertificates();
		}
	
}
