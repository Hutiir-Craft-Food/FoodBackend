package com.khutircraftubackend.confirm;

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
    private static final Long RE_UPDATE_TOKEN = 2L;

    public void sendVerificationEmail(UserEntity user, boolean update) {
        String verificationCode = getRandomNumber();
        emailSender.sendSimpleMessage(user.getEmail(),
                ConfirmResponseMessages.VERIFICATION_CODE_SUBJECT,
                String.format(
                        ConfirmResponseMessages.VERIFICATION_CODE_TEXT, verificationCode));

        saveConfirmToken(verificationCode, user, update);
    }

    private void saveConfirmToken(String token, UserEntity user, boolean update) {
        ConfirmEntity confirmEntity = update
                ? updateExistingToken(token, user)
                : createNewToken(token, user);
        confirmRepository.save(confirmEntity);
    }

    private ConfirmEntity updateExistingToken(String token, UserEntity user) {
        ConfirmEntity existingConfirm = findConfirmByUser(user);
        return setTokenProperties(existingConfirm, token);
    }

    private ConfirmEntity createNewToken(String token, UserEntity user) {
        ConfirmEntity newConfirm = ConfirmEntity.builder()
                .user(user)
                .build();
        return setTokenProperties(newConfirm, token);
    }

    private ConfirmEntity setTokenProperties(ConfirmEntity confirmEntity, String token) {
        confirmEntity.setConfirmationToken(token);
        confirmEntity.setCreatedAt(LocalDateTime.now());
        confirmEntity.setExpiresAt(LocalDateTime.now().plusMinutes(LIFE_TOKEN));
        return confirmEntity;
    }

    private ConfirmEntity findConfirmByUser(UserEntity user) {
        return confirmRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmResponseMessages.CONFIRM_NOT_FOUND));
    }

    private String getRandomNumber() {
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }

    @Transactional
    public ConfirmResponse confirmToken(Principal principal, ConfirmRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        if (user.isConfirmed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmResponseMessages.EMAIL_ACCEPTED);
        }
        ConfirmEntity confirm = findConfirmByUser(user);
        validateConfirmationToken(request.confirmationToken(), confirm);
        user.setConfirmed(true);
        confirmRepository.delete(confirm);
        userService.updateUser(user);
        return ConfirmResponse.builder()
                .confirmed(true)
                .message(ConfirmResponseMessages.CONFIRM_ACCEPTED)
                .build();
    }

    private void validateConfirmationToken(String key, ConfirmEntity confirm) {
        if (Boolean.FALSE.equals(
                key.equals(confirm.getConfirmationToken()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmResponseMessages.CONFIRM_NOT_FOUND);
        }

        if (confirm.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ConfirmResponseMessages.CONFIRM_TIME_IS_UP);
        }
    }

    public void reConfirmToken(Principal principal) {
        UserEntity user = userService.findByPrincipal(principal);
        ConfirmEntity confirm = findConfirmByUser(user);

        if (LocalDateTime.now().isBefore(confirm.getCreatedAt().plusMinutes(RE_UPDATE_TOKEN))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FAIL - Временная ошибка, до рефактора ошибок!");
        }

        sendVerificationEmail(user, true);
    }
}