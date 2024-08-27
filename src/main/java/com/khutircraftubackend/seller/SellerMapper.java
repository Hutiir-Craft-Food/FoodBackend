package com.khutircraftubackend.seller;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Інтерфейс SellerMapper мапить дані між моделлю Seller та DTO SellerDTO.
 */

@Mapper
public interface SellerMapper {
    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    SellerEntity SellerDTOToSeller(SellerDTO sellerDTO);

    SellerDTO SellerToSellerDTO(SellerEntity seller);
}
