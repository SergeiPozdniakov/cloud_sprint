package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.UserRepository;
import com.clouds.cloud_sprint.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() throws Exception {
        Users user = new Users();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        Users createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testGetUserByUsername() {
        Users user = new Users();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<Users> foundUser = userService.getUserByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }
}
