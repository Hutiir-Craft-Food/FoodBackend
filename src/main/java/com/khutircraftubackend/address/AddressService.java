package com.khutircraftubackend.address;

import com.khutircraftubackend.address.exception.address.AccessDeniedException;
import com.khutircraftubackend.address.exception.address.AddressNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
	
	private final AddressRepository addressRepository;
	private final AddressMapper addressMapper;
	
	public AddressEntity findAddressById(Long id) {
		
		return addressRepository.findById(id)
				.orElseThrow(() -> new AddressNotFoundException("Address with id " + id + " not found"));
	}
	
	@Transactional
    public AddressEntity createAddress(AddressRequest request) {
		
        return addressRepository.save(addressMapper.toAddressEntity(request));
	}
	
	@Transactional
	public AddressEntity updateAddress(Long id, AddressRequest request) {

		AddressEntity currentEntity = findAddressById(id);
		
		if (!currentEntity.getId().equals(id)) {
			throw new AccessDeniedException("Address with id: " + id + " already exists");//TODO Verify this method into correct and output in a separate method
		}

        addressMapper.updateAddressFromRequest(currentEntity, request);

        return addressRepository.save(currentEntity);
	}

	@Transactional
    public void deleteAddress(Long id) {

        AddressEntity currentEntity = findAddressById(id);
		
		if(!currentEntity.getId().equals(id)) {
			throw new AccessDeniedException("Address with id: " + id + " already exists");//TODO Verify this method into correct and output in a separate method
		}

		addressRepository.deleteById(currentEntity.getId());
    }

}
