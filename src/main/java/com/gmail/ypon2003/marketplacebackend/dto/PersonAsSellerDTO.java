package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;

@Builder
public record PersonAsSellerDTO (
     String name,
     String lastName,
     String password,
     String role,
     String email,
     String phoneNumber) {
}
