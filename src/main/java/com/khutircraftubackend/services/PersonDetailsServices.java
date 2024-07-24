package com.khutircraftubackend.services;

import com.khutircraftubackend.models.User;
import com.khutircraftubackend.repositories.UserRepository;
import com.khutircraftubackend.security.PersonDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Клас PersonDetailsServices реалізує бізнес-логіку для роботи з деталями користувача.
 */

@Service
@Slf4j
public class PersonDetailsServices implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Searching for user with email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            log.info("User found: {}", user.get());
        } else {
            log.info("User not found with email: {}", email);
        }
        return user.map(PersonDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Not found " + email));
    }
}
