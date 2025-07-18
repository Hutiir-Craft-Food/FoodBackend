package com.khutircraftubackend.user;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
