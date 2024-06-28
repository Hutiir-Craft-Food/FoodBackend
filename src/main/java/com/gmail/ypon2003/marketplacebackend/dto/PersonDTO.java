package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;

@Builder
public record PersonDTO(
     String email,
     String password,
     String  role) {
}
