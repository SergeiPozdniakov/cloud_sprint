package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.controller.HomeController;
import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IntTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_FILE_NAME = "testFile.txt";
    private static final String TEST_USER_FOLDER = "testFolder";
    private static final String TEST_FILE_CONTENT = "test content";
    private static final String BASE_URL = "/home";

    private MockMvc mockMvc;

    @Mock
    private FileService fileService;

    @InjectMocks
    private HomeController homeController;

    private Users testUser;

    @BeforeEach
    @DisplayName("Инициализация виртуального тестового пользователя")
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
        testUser = createTestUser();
    }

    private Users createTestUser() {
        Users user = new Users();
        user.setBaseFolderPath(TEST_USER_FOLDER);
        user.setId(TEST_USER_ID);
        return user;
    }

    @Test
    @DisplayName("Проверка добавления файла в облако")
    void addFile() throws Exception {
        MockMultipartFile testFile = new MockMultipartFile("file", TEST_FILE_NAME, "text/plain", TEST_FILE_CONTENT.getBytes());

        mockMvc.perform(multipart(BASE_URL + "/upload")
                        .file(testFile)
                        .principal(() -> testUser.getUsername()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Проверка успешного скачивания файла")
    void downloadFile_Success() throws Exception {
        File testFile = new File();
        testFile.setId(TEST_USER_ID);
        testFile.setFileName(TEST_FILE_NAME);
        testFile.setFilePath(TEST_USER_FOLDER + "/" + TEST_FILE_NAME);


        Path filePath = Paths.get(TEST_USER_FOLDER, TEST_FILE_NAME);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, TEST_FILE_CONTENT.getBytes());


        when(fileService.getFileById(testFile.getId())).thenReturn(testFile);


        mockMvc.perform(get(BASE_URL + "/download/{id}", testFile.getId())
                        .principal(() -> testUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + TEST_FILE_NAME + "\""))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"));

        verify(fileService, times(1)).getFileById(testFile.getId());


        Files.deleteIfExists(filePath);
    }


    @Test
    @DisplayName("Проверка успешного удаления файла")
    void deleteFile_Success() throws Exception {
         mockMvc.perform(post(BASE_URL + "/delete/{id}", (TEST_USER_ID))
                        .principal(() -> testUser.getUsername()))
                .andExpect(status().is3xxRedirection());

            verify(fileService, times(1)).deleteFile(TEST_USER_ID);
    }


}