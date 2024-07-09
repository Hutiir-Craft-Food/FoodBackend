package com.gmail.ypon2003.marketplacebackend.dtos;

import java.time.LocalDateTime;

public record ProductDTO(
        Long id,
        String name,
        Double price,
        LocalDateTime createDate,
        String sellerName
) {
}
