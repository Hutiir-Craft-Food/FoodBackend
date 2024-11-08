package com.khutircraftubackend.delivery;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/v1/sellers/{sellerId}/delivery-methods")
@RequiredArgsConstructor
public class DeliveryMethodController {
	
	private final DeliveryMethodService deliveryMethodService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Collection<DeliveryMethodResponse> getDeliveryMethods(
			@PathVariable Long sellerId) {
		
		return deliveryMethodService.getDeliveryMethodsBySeller(sellerId);
	}
	
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public DeliveryMethodResponse addDeliveryMethod(
			@PathVariable Long sellerId,
			@Valid @RequestBody DeliveryMethodRequest request) {
		
		return deliveryMethodService.addDeliveryMethod(sellerId, request);
	}
	
	@PutMapping(value = "/delivery/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@ResponseStatus(HttpStatus.OK)
	public DeliveryMethodResponse updateDeliveryMethod(
			@PathVariable Long id,
			@PathVariable Long sellerId,
			@Valid @RequestBody DeliveryMethodRequest request) {
		
		return deliveryMethodService.updateDeliveryMethod(sellerId, id, request);
	}
	
	@DeleteMapping("/delivery/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDeliveryMethod(
			@PathVariable Long sellerId,
			@PathVariable Long id) {
		
		deliveryMethodService.deleteDeliveryMethod(sellerId, id);
	}
	
}
