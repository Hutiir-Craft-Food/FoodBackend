package com.khutircraftubackend.services;

import com.khutircraftubackend.models.User;
import com.khutircraftubackend.repositories.UserRepository;
import com.khutircraftubackend.security.PersonDetails;
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
public class PersonDetailsServices implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(PersonDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Not found " + email));
    }
}
