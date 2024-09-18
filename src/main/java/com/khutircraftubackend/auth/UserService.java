package com.khutircraftubackend.auth;

import com.khutircraftubackend.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity findByPrincipal(Principal principal) {
        String email = principal.getName();
        return findByEmail(email);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
               .orElseThrow(() -> new UserNotFoundException(String.format(AuthResponseMessages.USER_NOT_FOUND, email)));
    }
}
