package com.khutircraftubackend.exception;

import com.khutircraftubackend.auth.AuthenticationService;
import com.khutircraftubackend.auth.request.LoginRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class OverridingTestConfig {

    @Bean
    public AuthenticationService authenticationService() {
        AuthenticationService mockSerice = mock(AuthenticationService.class);

        given(mockSerice.authenticate(any(LoginRequest.class)))
                .willAnswer(invokation -> {
                    throw new Exception("some unknown and unexpected exception causing 500 error");
                });

        return mockSerice;
    }
}