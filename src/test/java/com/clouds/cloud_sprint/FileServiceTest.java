package com.clouds.cloud_sprint;

import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.services.FileService;

import com.clouds.cloud_sprint.model.FileUploadProgressListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.clouds.cloud_sprint.model.FileUploadProgressListener;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileUploadProgressListener progressListener;

    @InjectMocks
    private FileService fileService;

    private MultipartFile multipartFile;
    private Users user;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this); // Инициализируем моки
        // Создаем тестовый файл
        multipartFile = new MockMultipartFile(
                "test.mp4", // Имя файла
                "test.mp4", // Оригинальное имя файла
                "video/mp4", // Тип файла
                "test data".getBytes() // Содержимое файла
        );

        // Создаем тестового пользователя
        user = new Users();
        user.setBaseFolderPath("C:\\cloud_storage\\testuser");
    }

    @Test
    void testAddFile() throws Exception {
        // Настройка моков
        doNothing().when(progressListener).reset(); // Мокируем reset()
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
            File file = invocation.getArgument(0);
            file.setId(1L); // Устанавливаем ID для сохраненного файла
            return file;
        });

        // Вызов метода
        CompletableFuture<File> future = fileService.addFile(multipartFile, user);

        // Проверка результата
        assertNotNull(future.join());
        assertEquals("test.mp4", future.join().getFileName());
        assertEquals("video/mp4", future.join().getContentType());
        assertEquals(9L, future.join().getFileSize()); // Размер файла "test data".getBytes() = 9 байт

        // Проверка вызовов
        verify(progressListener, times(1)).reset(); // Проверяем, что reset() был вызван
        verify(progressListener, atLeastOnce()).update(anyLong(), anyLong()); // Проверяем, что update() был вызван
        verify(fileRepository, times(1)).save(any(File.class)); // Проверяем, что save() был вызван
    }


    @Test
    void testGetFilesByUser() {
        Users user = new Users();
        when(fileRepository.findByUser(user)).thenReturn(List.of(new File()));

        List<File> files = fileService.getFilesByUser(user);

        assertFalse(files.isEmpty());
    }

    @Test
    void testDeleteFile() {
        File file = new File();
        file.setFilePath("C:\\cloud_storage\\testuser\\test.mp4");

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        CompletableFuture<Void> future = fileService.deleteFile(1L);

        assertNull(future.join());
        verify(fileRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFileModel() {
        File file = new File();
        file.setId(1L);
        file.setFileName("test.mp4");
        file.setContentType("video/mp4");
        file.setFileSize(1024L);
        file.setFilePath("C:\\cloud_storage\\testuser\\test.mp4");

        assertEquals(1L, file.getId());
        assertEquals("test.mp4", file.getFileName());
        assertEquals("video/mp4", file.getContentType());
        assertEquals(1024L, file.getFileSize());
        assertEquals("C:\\cloud_storage\\testuser\\test.mp4", file.getFilePath());
    }

    @Test
    void testUsersModel() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBaseFolderPath("C:\\cloud_storage\\testuser");

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("C:\\cloud_storage\\testuser", user.getBaseFolderPath());
    }


}
