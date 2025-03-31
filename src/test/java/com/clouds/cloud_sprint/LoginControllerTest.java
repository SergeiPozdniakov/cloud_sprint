package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.controller.LoginController;
import com.clouds.cloud_sprint.security.SecurityConfig;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class) // Импортируем конфигурацию безопасности
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk()) // Ожидаем статус 200
                .andExpect(view().name("login")) // Ожидаем имя представления "login"
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Вход в облачное хранилище"))); // Дополнительная проверка содержимого страницы
    }
}