package com.khutircraftubackend.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Клас UserDetailsServices реалізує бізнес-логіку для роботи з деталями користувача.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       UserEntity userEntity = userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
       log.info("user: {}", UserDetailsImpl.build(userEntity));
               return UserDetailsImpl.build(userEntity);
    }
}