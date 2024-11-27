package com.khutircraftubackend.security;

import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
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
               
       return new  UserDetailsImpl(user);
    }
}
