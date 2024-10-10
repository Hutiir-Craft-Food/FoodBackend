package com.khutircraftubackend.security;

import com.khutircraftubackend.auth.UserEntity;
import com.khutircraftubackend.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Клас UserDetailsServices реалізує бізнес-логіку для роботи з деталями користувача.
 */

@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       UserEntity user = userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
       log.info("user: {}", UserDetailsImpl.build(user));
               return UserDetailsImpl.build(user);
    }
}