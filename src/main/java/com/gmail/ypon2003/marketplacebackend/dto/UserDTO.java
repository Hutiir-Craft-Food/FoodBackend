package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;

import java.util.List;

@Builder//Чи потрібний даний патерн?
public record UserDTO(Long id,
                      String name,
                      String lastName,
                      String email,
                      String phoneNumber,
                      String password,
                      List<ProductDTO> productDTOList) {
}
