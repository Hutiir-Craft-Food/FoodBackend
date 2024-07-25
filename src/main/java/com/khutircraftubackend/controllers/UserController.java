package com.khutircraftubackend.controllers;

import com.khutircraftubackend.dto.UserDTO;
import com.khutircraftubackend.dto.security.JwtResponse;
import com.khutircraftubackend.dto.security.LoginRequest;
import com.khutircraftubackend.dto.security.PasswordRecoveryRequest;
import com.khutircraftubackend.dto.security.PasswordUpdateRequest;
import com.khutircraftubackend.security.JwtUtils;
import com.khutircraftubackend.services.PersonDetailsServices;
import com.khutircraftubackend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Клас UserController обробляє запити, пов'язані з користувачами.
 */

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PersonDetailsServices personDetailsServices;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Аутентифікує користувача на основі даних для входу.
     *
     * @param loginRequest запит з даними для входу (email і пароль)
     * @return відповідь з JWT токеном або статусом помилки, якщо аутентифікація не вдалася
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if(authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Виконує аутентифікацію користувача на основі email і пароля.
     *
     * @param email email користувача
     * @param password пароль користувача
     * @return Authentication об'єкт, якщо аутентифікація пройшла успішно, або null, якщо не вдалася
     */
    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = personDetailsServices.loadUserByUsername(email);

        if(userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            return null;
        }
    }

    /**
     * Реєструє нового користувача.
     *
     * @param userDTO дані для реєстрації користувача
     * @return відповідь з даними зареєстрованого користувача або статусом помилки, якщо користувач з таким email вже існує
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        if(userService.findUserByEmail(userDTO.email()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserDTO registeredUser = userService.registerNewUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {

        boolean isUpdated = userService.updatePassword(passwordUpdateRequest);
        if(isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Ініціює відновлення пароля для користувача.
     *
     * @param passwordRecoveryRequest запит з даними для відновлення пароля (email користувача)
     * @return відповідь з HTTP статусом 200 (OK) після успішної ініціації відновлення пароля
     */
    @PostMapping("/recovery")
    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody PasswordRecoveryRequest passwordRecoveryRequest) {
        userService.initiatePasswordRecovery(passwordRecoveryRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Підтверджує обліковий запис користувача за допомогою коду підтвердження.
     * <p>
     * Цей метод обробляє GET-запит для підтвердження електронної пошти користувача.
     * Використовуючи код підтвердження, користувач активується у системі.
     * </p>
     *
     * @param code код підтвердження, який був надісланий на електронну пошту користувача
     * @return відповідь з HTTP статусом 200 (OK), якщо підтвердження пройшло успішно
     */
    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmUser(@RequestParam String code) {
        userService.enableUser(code);
        return ResponseEntity.ok().build();
    }
}
