package com.khutircraftubackend.confirm;

import com.khutircraftubackend.confirm.exception.ConfirmationException;
import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final Random random = new Random();
    private final EmailSender emailSender;
    private final ConfirmRepository confirmRepository;
    private final UserService userService;
    private static final byte LIFE_TOKEN = 5;
    private static final Long RE_UPDATE_TOKEN = 2L;

    public void sendVerificationEmail(UserEntity user, boolean isUpdateOrCreateToken) {
        String verificationCode = generateCode();
        emailSender.sendSimpleMessage(user.getEmail(),
                ConfirmationResponseMessages.VERIFICATION_CODE_SUBJECT,
                String.format(
                        ConfirmationResponseMessages.VERIFICATION_CODE_TEXT, verificationCode));

        saveConfirmationToken(verificationCode, user, isUpdateOrCreateToken);
    }
    /**
     * Method to create or update a token in the database.
     * Метод для створення або оновлення токена в базі даних
     * @param token - A token that is written to the database.
     * @param user - The identity of the user to whom this token is assigned.
     * @param isUpdateOrCreateToken - Parameter indicating whether to update or create a new token.
     *                              "true" - update, "false" - create.
     */
    private void saveConfirmationToken(String token, UserEntity user, boolean isUpdateOrCreateToken) {
        ConfirmationEntity confirmationEntity = isUpdateOrCreateToken
                ? updateExistingToken(token, user)
                : createUserConfirmationToke(token, user);
        confirmRepository.save(confirmationEntity);
    }

    private ConfirmationEntity updateExistingToken(String token, UserEntity user) {
        ConfirmationEntity confirmedByUser = findConfirmByUser(user);
        return setTokenProperties(confirmedByUser, token);
    }

    private ConfirmationEntity createUserConfirmationToke(String token, UserEntity user) {
        ConfirmationEntity newConfirmedByUser = ConfirmationEntity.builder()
                .user(user)
                .build();
        return setTokenProperties(newConfirmedByUser, token);
    }

    private ConfirmationEntity setTokenProperties(ConfirmationEntity confirmEntity, String token) {
        confirmEntity.setConfirmationToken(token);
        confirmEntity.setCreatedAt(LocalDateTime.now());
        confirmEntity.setExpiresAt(LocalDateTime.now().plusMinutes(LIFE_TOKEN));
        return confirmEntity;
    }

    private ConfirmationEntity findConfirmByUser(UserEntity user) {
        return confirmRepository.findByUser(user)
                .orElseThrow(() ->
                        new ConfirmationException(ConfirmationResponseMessages.CONFIRMATION_TOKEN_INVALID));
    }

    private String generateCode() {
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }

    @Transactional
    public ConfirmationResponse confirmToken(Principal principal, ConfirmationRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        if (user.isConfirmed()) {
            throw new ConfirmationException(ConfirmationResponseMessages.EMAIL_ALREADY_CONFIRMED);
        }
        ConfirmationEntity confirmedByUser = findConfirmByUser(user);
        validateConfirmationToken(request.confirmationToken(), confirmedByUser);
        user.setConfirmed(true);
        confirmRepository.delete(confirmedByUser);
        userService.updateUser(user);
        return ConfirmationResponse.builder()
                .confirmed(true)
                .message(ConfirmationResponseMessages.EMAIL_CONFIRMED)
                .build();
    }

    private void validateConfirmationToken(String requestConfirmedToken, ConfirmationEntity existingToken) {
        if (Boolean.FALSE.equals(
                requestConfirmedToken.equals(existingToken.getConfirmationToken()))) {
            throw new ConfirmationException(ConfirmationResponseMessages.CONFIRMATION_TOKEN_INVALID);
        }

        if (existingToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ConfirmationException(ConfirmationResponseMessages.CONFIRMATION_TOKEN_EXPIRED);
        }
    }

    @Transactional
    public void reConfirmToken(Principal principal) {
        UserEntity user = userService.findByPrincipal(principal);
        ConfirmationEntity confirmedByUser = findConfirmByUser(user);

        if (LocalDateTime.now().isBefore(confirmedByUser.getCreatedAt().plusMinutes(RE_UPDATE_TOKEN))) {
            throw new ConfirmationException(ConfirmationResponseMessages.WAIT_FOR_NEXT_ATTEMPT);
        }

        sendVerificationEmail(user, true);
    }
}