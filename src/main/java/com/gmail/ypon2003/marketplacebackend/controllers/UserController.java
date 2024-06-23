package com.gmail.ypon2003.marketplacebackend.controllers;

import com.gmail.ypon2003.marketplacebackend.dto.UserDTO;
import com.gmail.ypon2003.marketplacebackend.models.User;
import com.gmail.ypon2003.marketplacebackend.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author uriiponomarenko 31.05.2024
 */
@Tag(name = "Керування користувачами", description = "Операції, пов'язані з керуванням користувачами")
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Operation(summary = "Отримання списку з усіма користувачами", description = "Повернення списку користувачів")
    @GetMapping
    public List<UserDTO> getAll() {
        return userServiceImpl.getByAllUsers();
    }

    @Operation(summary = "Отримання користувача за id із БД")
    @GetMapping("/show_user")
    public void showUser(Long id) {
        userServiceImpl.getBiIdUser(id);
    }

    @Operation(summary = "Додавання нових користувачів в БД")
    @PostMapping("/add_user")
    public void createUser(@Valid @RequestBody User user) {
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());
    }

    @Operation(summary = "Оновлення існуючих користувачів в БД")
    @PutMapping("/user/{id}")
    public void update(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        userServiceImpl.updateUser(id, user);
    }

    @Operation(summary = "Видалення користувачів із БД")
    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable("id") Long id) {
        userServiceImpl.deleteUser(id);
    }
}
