package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.services.UserService;
import com.clouds.cloud_sprint.controller.SignupController;
import com.clouds.cloud_sprint.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignupController.class)
@Import(SecurityConfig.class) // Импортируем SecurityConfig
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testSignup() throws Exception {
        Users user = new Users();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.getUserByUsername("testuser")).thenReturn(Optional.empty());
        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/signup")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .with(csrf())) // Добавляем CSRF-токен
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }



}