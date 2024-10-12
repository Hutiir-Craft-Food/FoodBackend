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
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@Setter
@Getter
public class UserProfileService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final EmailSender emailSender;
    private Map<UserEntity, String> confirmEmail;

    public ChangeOfEmailResponse changeOfEmail(Principal principal, ChangeOfEmailRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        if (Boolean.FALSE.equals(equalsEmailOrConfirm(request.oldEmail(), user.getEmail()))) {
            throw new IllegalArgumentException("Invalid old email");
        }
        String randomNumber = authenticationService.getRandomNumber(); //вынести метод в юзерсервисе -> getRandomNumber
        emailSender.sendSimpleMessage(request.newEmail(), "NEW CONFIRM TOKEN", randomNumber);
        confirmEmail.put(user, randomNumber);
        return ChangeOfEmailResponse.builder()
                .newEmail(request.newEmail())
                .confirmToken(randomNumber)
                .build();
    }

    private Boolean equalsEmailOrConfirm(String request, String user) {
        return user.equals(request);
    }

    @Transactional
    public void confirmEmail(Principal principal, ConfirmEmailRequest request) {
        UserEntity user = userService.findByPrincipal(principal);
        String confirmToken = confirmEmail.get(user);
        if (Boolean.FALSE.equals(equalsEmailOrConfirm(request.confirmToken(), confirmToken))) {
            throw new IllegalArgumentException("Invalid confirm Token");
        }

        user.setEmail(request.newEmail());
        userRepository.save(user);
        cleanMap(user);
    }

    private void cleanMap(UserEntity user) {
        confirmEmail.remove(user);
    }
}
