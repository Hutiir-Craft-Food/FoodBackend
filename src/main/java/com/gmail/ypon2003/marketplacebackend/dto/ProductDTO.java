package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record ProductDTO(
        String name,
        String description,
        BigDecimal price,
        String measurement,
        Date createAt,
        String infoSeller
) {
}
