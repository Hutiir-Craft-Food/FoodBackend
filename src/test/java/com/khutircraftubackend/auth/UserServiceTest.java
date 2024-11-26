package com.khutircraftubackend.auth;


import com.khutircraftubackend.user.Role;
import com.khutircraftubackend.user.UserEntity;
import com.khutircraftubackend.user.UserRepository;
import com.khutircraftubackend.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

//    @Test
//    void testGetUserForEmail_UserExists() {
//        String email = "test@test.com";
//        UserEntity expectedUser = UserEntity.builder()
//                .email(email)
//                .enabled(true)
//                .role(Role.SELLER)
//                .build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));
//
//        UserEntity actualUser = userService.findByEmail(email);
//
//        assertEquals(expectedUser, actualUser);
//        verify(userRepository, times(1)).findByEmail(email);
//    }
//
//    @Test
//    void testGetUserForEmail_UserNotFound() {
//        String email = "notfound@test.com";
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(email));
//
//        verify(userRepository, times(1)).findByEmail(email);
//    }
}
