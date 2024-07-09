package com.gmail.ypon2003.marketplacebackend.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record BuyerDTO(
        Long id,
        String lastname,
        String firstname,
        LocalDateTime createDate,
        String phone,
        String password,
        List<Integer> products,
        String email
) {}