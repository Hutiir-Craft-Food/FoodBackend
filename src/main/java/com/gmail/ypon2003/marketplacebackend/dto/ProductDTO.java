package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductDTO(
        String name,
        String description,
        BigDecimal price,
        String measurement,
        LocalDateTime createAt,
        String infoSeller,
        Long personId
) {
}
