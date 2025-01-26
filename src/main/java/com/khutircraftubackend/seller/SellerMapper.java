package com.khutircraftubackend.seller;

import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Інтерфейс SellerMapper мапить дані між моделлю SellerEntity та DTO SellerDTO.
 */

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface SellerMapper {
    SellerEntity SellerDTOToSeller(SellerDTO sellerDTO);
    
    SellerResponse toSellerResponse(SellerEntity sellerEntity);
}
