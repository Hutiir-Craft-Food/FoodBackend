package com.khutircraftubackend.confirm;

import com.khutircraftubackend.auth.AuthResponseMessages;
import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmService {

    private final Random random = new Random();
    private final EmailSender emailSender;
    private final ConfirmRepository confirmRepository;
    private final UserService userService;
    private static final int LIFE_TOKEN = 5;

    public void sendVerificationEmail(UserEntity user) {
        String verificationCode = getRandomNumber();
        emailSender.sendSimpleMessage(user.getEmail(),
                AuthResponseMessages.VERIFICATION_CODE_SUBJECT,
                String.format(
                        AuthResponseMessages.VERIFICATION_CODE_TEXT, verificationCode));

        saveConfirmToken(verificationCode, user);
    }

    private void saveConfirmToken(String token, UserEntity user) {
        ConfirmEntity confirm = ConfirmEntity.builder()
                .confirmationToken(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(LIFE_TOKEN))
                .build();
        confirmRepository.save(confirm);
    }

    private ConfirmEntity findByUser(UserEntity user) {
        return confirmRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmMessageResponse.CONFIRM_NOT_FOUND));
    }

    private String getRandomNumber() {
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }

    @Transactional
    public ConfirmResponse confirmToken(Principal principal, ConfirmRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        if (user.isConfirmed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmMessageResponse.EMAIL_ACCEPTED);
        }
        ConfirmEntity confirm = findByUser(user);
        validateConfirmationToken(request.confirmationToken(), confirm);
        user.setConfirmed(true);
        confirmRepository.delete(confirm);
        userService.updateUser(user);
        return ConfirmResponse.builder()
                .confirmed(true)
                .message(ConfirmMessageResponse.CONFIRM_ACCEPTED)
                .build();
    }

    private void validateConfirmationToken(String key, ConfirmEntity confirm) {
        if (Boolean.FALSE.equals(
                key.equals(confirm.getConfirmationToken()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmMessageResponse.CONFIRM_NOT_FOUND);
        }

        if (confirm.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmMessageResponse.CONFIRM_TIME_IS_UP);
        }
    }
}
