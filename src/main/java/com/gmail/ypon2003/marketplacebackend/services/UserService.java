package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.dto.UserDTO;
import com.gmail.ypon2003.marketplacebackend.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO getBiIdUser(Long id);
    User getById(Long id);
    Optional<User> getByEmail(String email);
    List<UserDTO> getByAllUsers();
}
