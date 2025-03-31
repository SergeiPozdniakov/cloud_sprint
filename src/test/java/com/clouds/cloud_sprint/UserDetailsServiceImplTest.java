package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.security.UserDetailsServiceImpl;
import com.clouds.cloud_sprint.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername() {
        Users user = new Users();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Users result = (Users) userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
