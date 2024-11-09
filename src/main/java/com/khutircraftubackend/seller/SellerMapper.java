package com.khutircraftubackend.seller;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Інтерфейс SellerMapper мапить дані між моделлю SellerEntity та DTO SellerDTO.
 */

@Mapper(componentModel = "spring")
public interface SellerMapper {
    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    SellerEntity SellerDTOToSeller(SellerDTO sellerDTO);

    SellerDTO SellerToSellerDTO(SellerEntity seller);
    
    SellerResponse toSellerResponse(SellerEntity sellerEntity);
    
    default Collection<SellerResponse> toSellerResponse(Collection<SellerEntity> sellerEntities) {
        return sellerEntities.stream()
                .map(this::toSellerResponse)
                .collect(Collectors.toList());
    }
}
