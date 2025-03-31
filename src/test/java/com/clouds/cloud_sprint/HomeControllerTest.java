package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.services.FileService;
import com.clouds.cloud_sprint.controller.HomeController;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

//import java.io.File;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void testHome() throws Exception {
        // Создаем пользователя
        Users user = new Users();
        user.setUsername("testuser");

        // Настраиваем SecurityContext
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
        SecurityContextHolder.setContext(securityContext);

        // Настраиваем FileService
        when(fileService.getFilesByUser(any())).thenReturn(Collections.emptyList());

        // Выполняем запрос
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk()) // Ожидаем статус 200
                .andExpect(view().name("home")) // Ожидаем имя представления "home"
                .andExpect(model().attributeExists("files")); // Ожидаем наличие атрибута "files"
    }

    @Test
    void testUploadFile() throws Exception {
        Users user = new Users();
        user.setUsername("testuser");

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
        SecurityContextHolder.setContext(securityContext);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp4", "video/mp4", "test content".getBytes()
        );

        // Создаем объект вашего класса File
        File savedFile = new File();
        savedFile.setId(1L); // Устанавливаем ID
        savedFile.setFileName("test.mp4"); // Устанавливаем имя файла
        savedFile.setFilePath("C:\\cloud_storage\\testuser\\test.mp4"); // Устанавливаем путь

        // Мокируем вызов fileService.addFile
        when(fileService.addFile(any(), any())).thenReturn(CompletableFuture.completedFuture(savedFile));

        mockMvc.perform(multipart("/home/upload").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
