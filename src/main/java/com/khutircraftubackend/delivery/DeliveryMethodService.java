package com.khutircraftubackend.delivery;

import com.khutircraftubackend.delivery.exception.delivery.DeliveryMethodNotFoundException;
import com.khutircraftubackend.delivery.exception.delivery.InvalidDeliveryArgumentException;
import com.khutircraftubackend.seller.SellerEntity;
import com.khutircraftubackend.seller.SellerRepository;
import com.khutircraftubackend.seller.exception.seller.SellerNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DeliveryMethodService {
	
	private final DeliveryMethodRepository deliveryMethodRepository;
	private final DeliveryMethodMapper deliveryMethodMapper;
	private final SellerRepository sellerRepository;
	
	
	private SellerEntity getSellerById(Long id) {
		
		return sellerRepository.findById(id)
				.orElseThrow(() -> new SellerNotFoundException("Seller not found"));
	}
	
	private DeliveryMethodEntity getDeliveryMethodById(Long id) {
		
		return deliveryMethodRepository.findById(id)
				.orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery method not found"));
	}
	
	
	public Collection<DeliveryMethodResponse> getDeliveryMethodsBySeller(Long sellerId) {
		
		Collection<DeliveryMethodEntity> methods = deliveryMethodRepository.findBySellerId(sellerId);
		
		return deliveryMethodMapper.toDeliveryMethodResponse(methods);
	}
	
	@Transactional
	public DeliveryMethodResponse addDeliveryMethod(Long sellerId, DeliveryMethodRequest request) {
		
		SellerEntity seller = getSellerById(sellerId);
		
		DeliveryMethodEntity method = deliveryMethodMapper.toDeliveryMethodEntity(request);
		method.setSeller(seller);
		
		return deliveryMethodMapper.toDeliveryMethodResponse(deliveryMethodRepository.save(method));
		
	}
	
	@Transactional
	public DeliveryMethodResponse updateDeliveryMethod(Long sellerId, Long methodId, DeliveryMethodRequest request) {
		
		SellerEntity seller = getSellerById(sellerId);
		
		DeliveryMethodEntity existingMethod = getDeliveryMethodById(methodId);
		
		if (!existingMethod.getSeller().equals(seller)) {
            throw new InvalidDeliveryArgumentException("Cannot update delivery method of another seller", seller.getId(), existingMethod.getId());
        }
		
		deliveryMethodMapper.updateDeliveryMethodFromRequest(existingMethod, request);
		existingMethod.setSeller(seller);
		
		return deliveryMethodMapper.toDeliveryMethodResponse(deliveryMethodRepository.save(existingMethod));
	}
	
	@Transactional
	public void deleteDeliveryMethod(Long sellerId, Long methodId) {
		
        SellerEntity seller = getSellerById(sellerId);
		
		DeliveryMethodEntity existingMethod = getDeliveryMethodById(methodId);
		
        if (!existingMethod.getSeller().equals(seller)) {
            throw new InvalidDeliveryArgumentException("Cannot delete delivery method of another seller", seller.getId(), existingMethod.getId());
        }
		
		existingMethod.setSeller(null);
		
		deliveryMethodRepository.deleteById(methodId);
	}
}
