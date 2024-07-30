package com.khutircraftubackend.mapper;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.models.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Інтерфейс SellerMapper мапить дані між моделлю Seller та DTO SellerDTO.
 */

@Mapper
public interface SellerMapper {
    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    Seller SellerDTOToSeller(SellerDTO sellerDTO);

    SellerDTO SellerToSellerDTO(Seller seller);
}
