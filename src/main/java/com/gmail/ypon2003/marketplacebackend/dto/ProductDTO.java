package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
public record ProductDTO(Long id,
                         String name,
                         String description,
                         BigDecimal price,
                         Data createAt,
                         String infoSeller,
                         String measurement) {
}
