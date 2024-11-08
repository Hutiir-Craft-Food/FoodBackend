package com.khutircraftubackend.address;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
	
	private final AddressService addressService;
	private final AddressMapper addressMapper;
	
//	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//	@ResponseStatus(HttpStatus.CREATED)
//	public AddressResponse createAddress(
//            @Valid @ModelAttribute AddressRequest request) {
//
//        AddressEntity newAddress = addressService.createAddress(request);
//
//        return addressMapper.toAddressResponse(newAddress);
//	}
	
//	@PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//	@ResponseStatus(HttpStatus.OK)
//	public AddressResponse updateAddress(
//            @PathVariable Long id,
//            @Valid @ModelAttribute AddressRequest request,
//            Principal principal) {
//
//        AddressEntity updatedAddress = addressService.updateAddress(id, request, principal);
//
//        return addressMapper.toAddressResponse(updatedAddress);
//	}
//
//	@DeleteMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteAddress(
//            @PathVariable Long id,
//            Principal principal) {
//
//        addressService.deleteAddress(id, principal);
//	}
}
