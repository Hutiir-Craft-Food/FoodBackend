package com.gmail.ypon2003.marketplacebackend.services;

import com.gmail.ypon2003.marketplacebackend.dto.UserDTO;
import com.gmail.ypon2003.marketplacebackend.factory.UserFactory;
import com.gmail.ypon2003.marketplacebackend.models.User;
import com.gmail.ypon2003.marketplacebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author uriiponomarenko 28.05.2024
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    @Override
    public UserDTO getBiIdUser(Long id) {
        User userId = getById(id);
        return userFactory.apply(userId);
    }

    @Override
    public User getById(Long id) {
        return userRepository.getReferenceById(id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Cacheable("users")
    public List<UserDTO> getByAllUsers() {
        return userRepository.findAll().stream()
                .map(userFactory)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateUser(Long id, User user) {
        user.setId(id);
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());
        user.setName(user.getName());
        user.setLastName(user.getLastName());
        user.setRole(user.getRole());
        user.setPhoneNumber(user.getPhoneNumber());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
