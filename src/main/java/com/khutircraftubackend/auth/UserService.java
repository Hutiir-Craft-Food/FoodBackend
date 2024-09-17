package com.khutircraftubackend.auth;

import com.khutircraftubackend.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity findUserForPrincipal(Principal principal) {
        String email = principal.getName();
        return findUserForEmail(email);
    }

    public UserEntity findUserForEmail(String email) {
        return userRepository.findByEmail(email)
               .orElseThrow(() -> new UserNotFoundException(String.format(AuthResponseMessages.USER_NOT_FOUND, email)));
    }
}
