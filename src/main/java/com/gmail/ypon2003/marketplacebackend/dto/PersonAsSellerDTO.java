package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PersonAsSellerDTO {
    private final String name;
    private final String lastName;
    private final String password;
    private final String role;
    private final String email;
    private final String phoneNumber;
}
