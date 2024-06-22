package com.gmail.ypon2003.marketplacebackend.factory;

import com.gmail.ypon2003.marketplacebackend.dto.UserDTO;
import com.gmail.ypon2003.marketplacebackend.models.User;
import com.gmail.ypon2003.marketplacebackend.util.WebIsNullFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/*
Клас UserFactory служить для перетворення моделі User
у DTO UserDTO.
 */
@Component
@RequiredArgsConstructor
public class UserFactory implements Function<User, UserDTO> {

    private final WebIsNullFactory webIsNullFactory;

    @Override
    public UserDTO apply(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .productDTOList(webIsNullFactory.isNullProductUser(user))
                .build();
    }
}
