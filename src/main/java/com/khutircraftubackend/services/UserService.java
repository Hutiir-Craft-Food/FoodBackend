package com.khutircraftubackend.services;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.mapper.UserMapper;
import com.khutircraftubackend.models.Role;
import com.khutircraftubackend.models.User;
import com.khutircraftubackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO registerNewUser(UserDTO userDTO) {
        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.SELLER);
        user.setEnabled(false);//до підтвердження електронною поштою
        User savedUser = userRepository.save(user);// надіслати сюди логіку електронного листа з підтвердженням
        return UserMapper.INSTANCE.userToUserDTO(savedUser);
    }

    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper.INSTANCE::userToUserDTO);
    }
    @Transactional
    public void enableUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
