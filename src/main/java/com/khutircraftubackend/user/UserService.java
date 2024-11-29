package com.khutircraftubackend.user;

import com.khutircraftubackend.auth.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity createdUser(RegisterRequest request) {
        UserEntity user = UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .role(request.role())
                .confirmed(false)
                .build();
        userRepository.saveAndFlush(user);
        return user;
    }

    public void updateUser (UserEntity user){
        userRepository.save(user);
    }

    public UserEntity findByPrincipal(Principal principal) {
        String email = principal.getName();
        return findByEmail(email);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
               .orElseThrow(() ->
                       new ResponseStatusException(HttpStatus.FORBIDDEN, UserResponseMessages.USER_NOT_FOUND));
    }
}
