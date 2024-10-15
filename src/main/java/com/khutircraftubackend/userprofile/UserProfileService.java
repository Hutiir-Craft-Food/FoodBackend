package com.khutircraftubackend.userprofile;

import com.khutircraftubackend.auth.AuthenticationService;
import com.khutircraftubackend.auth.UserEntity;
import com.khutircraftubackend.auth.UserRepository;
import com.khutircraftubackend.auth.UserService;
import com.khutircraftubackend.mail.EmailSender;
import com.khutircraftubackend.userprofile.request.ChangeOfEmailRequest;
import com.khutircraftubackend.userprofile.request.ConfirmEmailRequest;
import com.khutircraftubackend.userprofile.response.ChangeOfEmailResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Setter
@Getter
public class UserProfileService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final EmailSender emailSender;
    private Map<String, EmailTokenInfo> confirmEmail;

    public ChangeOfEmailResponse changeOfEmail(Principal principal, ChangeOfEmailRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        if (Boolean.FALSE.equals(equalsEmailOrConfirm(request.oldEmail(), user.getEmail()))) {
            throw new IllegalArgumentException("Invalid old email");
        }
        String randomNumber = authenticationService.getRandomNumber(); //вынести метод в юзерсервисе -> getRandomNumber
        emailSender.sendSimpleMessage(request.newEmail(), "NEW CONFIRM TOKEN", randomNumber);
        EmailTokenInfo emailTokenInfo = new EmailTokenInfo(randomNumber);
        confirmEmail.put(request.newEmail(), emailTokenInfo);
        return ChangeOfEmailResponse.builder()
                .newEmail(request.newEmail())
                .confirmToken(randomNumber)
                .build();
    }

    private Boolean equalsEmailOrConfirm(String request, String user) {
        return user.equals(request);
    }

    public void confirmEmail(Principal principal, ConfirmEmailRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        cleanExpiredEntries();
        EmailTokenInfo emailTokenInfo = confirmEmail.get(request.newEmail());
        if (Boolean.FALSE.equals(equalsEmailOrConfirm(request.confirmToken(), emailTokenInfo.getToken()))) {
            throw new IllegalArgumentException("Invalid confirm Token");
        }

        user.setEmail(request.newEmail());
        userRepository.save(user);
        cleanMap(request.newEmail());
    }

    private void cleanMap(String key) {
        confirmEmail.remove(key);
    }

    private void cleanExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();
        confirmEmail.entrySet().removeIf(entry ->
                Duration.between(entry.getValue().getTimestamp(), now).toMinutes() > 5);
    }
}
