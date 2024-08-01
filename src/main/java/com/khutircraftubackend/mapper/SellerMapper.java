package com.khutircraftubackend.mapper;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.models.SellerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SellerMapper {
    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    SellerEntity toSellerEntity(SellerDTO sellerDTO);

    SellerDTO toSellerDTO(SellerEntity sellerEntity);
}
