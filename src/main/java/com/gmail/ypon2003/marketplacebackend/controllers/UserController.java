package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dto.UserDTO;
import com.gmail.ypon2003.marketplacebackend.models.User;
import com.gmail.ypon2003.marketplacebackend.services.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uriiponomarenko 31.05.2024
 */
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping
    public List<UserDTO> getAll() {
        return userServiceImpl.getByAllUsers();
    }

    @GetMapping("/show_user")
    public void showUser(Long id) {
        userServiceImpl.getBiIdUser(id);
    }

    @PostMapping("/add_user")
    public void createUser(@Valid @RequestBody User user) {
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());
    }

    @PutMapping("/user/{id}")
    public void update(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        userServiceImpl.updateUser(id, user);
    }

    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable("id") Long id) {
        userServiceImpl.deleteUser(id);
    }
}
