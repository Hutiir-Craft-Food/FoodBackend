package com.khutircraftubackend.user;

import com.khutircraftubackend.auth.request.RegisterRequest;
import com.khutircraftubackend.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // TODO: remove redundant method
    public void updateUser (UserEntity user){
        userRepository.save(user);
    }

    /**
     * The method returns the user identity by Principal.
     * Метод возврашает сушность юзера по Principal.
     * @param principal the security context's authenticated user
     * @return UserEntity user
     */
    public UserEntity findByPrincipal(Principal principal) {
        String email = principal.getName();
        return findByEmail(email);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
               .orElseThrow(() ->
                       new NotFoundException(String.format(UserResponseMessages.USER_NOT_FOUND, email)));
    }

    /**
     * Method for checking whether the user's email is confirmed.
     * Метод для проверки подтверждена почта у юзера.
     * @param principal the security context's authenticated user
     * @return true or false
     */
    public boolean isUserMailConfirmed(Principal principal){
        UserEntity user = findByPrincipal(principal);
        return user.isConfirmed();
    }

    /**
     * Checks if the given user has the SELLER role.
     * This implementation is null-safe - won't throw NPE if user or role is null.
     * Перевіряє, чи має користувач роль SELLER.
     * @param user the user entity to check (maybe null)
     * @return true if user exists and has SELLER role, false otherwise
     */
    public boolean isSeller(UserEntity user){
        return Role.SELLER.equals(user.getRole());
    }
}
