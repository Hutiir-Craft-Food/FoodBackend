package com.khutircraftubackend.seller;

import com.khutircraftubackend.seller.request.SellerRequest;
import com.khutircraftubackend.seller.response.SellerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;

/**
 * Інтерфейс SellerMapper мапить дані між моделлю SellerEntity та DTO SellerRequest.
 */

@Mapper(componentModel = "spring")
public interface SellerMapper {
    SellerEntity toSellerEntity(SellerRequest sellerRequest);


    void updateSellerFromRequest(@MappingTarget SellerEntity seller, SellerRequest request);
    
    @Mapping(target = "phoneNumber", ignore = true)
    SellerResponse toSellerResponse(SellerEntity sellerEntity);
    
    Collection<SellerResponse> toSellerResponse(Collection<SellerEntity> sellerEntities);
    
}
