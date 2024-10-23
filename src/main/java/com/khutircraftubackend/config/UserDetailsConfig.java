package com.khutircraftubackend.config;

import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class UserDetailsConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        
        return new UserDetailsServiceImpl(userRepository);
    }
}
