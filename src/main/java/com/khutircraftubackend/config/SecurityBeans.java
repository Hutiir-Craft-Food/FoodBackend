package com.khutircraftubackend.config;

import com.khutircraftubackend.security.JwtUtils;
import com.khutircraftubackend.services.PersonDetailsServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new PersonDetailsServices();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }
}
