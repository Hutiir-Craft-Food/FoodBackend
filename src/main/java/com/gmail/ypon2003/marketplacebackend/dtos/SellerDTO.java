package com.gmail.ypon2003.marketplacebackend.dtos;

public record SellerDTO(
        Long id,
        String name,
        String email,
        String phone,
        LocalDateTime createDate,
        String password,
        List<Integer> products
) {
}
