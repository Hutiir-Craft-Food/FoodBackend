package com.gmail.ypon2003.marketplacebackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PersonDTO {
    private final String email;
    private final String password;
    private final String role;

}
