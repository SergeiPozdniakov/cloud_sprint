package com.clouds.cloud_sprint.services;

import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.model.FileUploadProgressListener;
import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.FileRepository;
import com.clouds.cloud_sprint.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileUploadProgressListener progressListener;

    @Transactional
    public CompletableFuture<File> addFile(MultipartFile file, Users users) {
        try {
            // Проверяем, что файл не пуст
            if (file == null || file.isEmpty()) {
                logger.error("Файл не был загружен или пуст");
                return CompletableFuture.failedFuture(new IllegalArgumentException("Файл не был загружен или пуст"));
            }

            // Сброс прогресса перед началом загрузки
            progressListener.reset();

            // Создаем директорию, если она не существует
            String userFolder = users.getBaseFolderPath();
            Path path = Paths.get(userFolder);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // Сохраняем файл на диск
            Path filePath = Paths.get(userFolder, file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // Обновляем прогресс
                    progressListener.update(progressListener.getBytesRead() + bytesRead, file.getSize());
                    // Записываем данные в файл
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            //Files.write(filePath, file.getBytes());

            // Создаем объект File и сохраняем его в базу данных
            File newFile = new File();
            newFile.setFileName(file.getOriginalFilename());
            newFile.setContentType(file.getContentType());
            newFile.setFileSize(file.getSize());
            newFile.setFilePath(filePath.toString());
            newFile.setUser(users);

            logger.info("Размер файла: {} байт", file.getSize());
            File savedFile = fileRepository.save(newFile);
            return CompletableFuture.completedFuture(savedFile);
        } catch (IOException e) {
            // Обработка ошибок ввода-вывода (например, проблемы с файловой системой)
            logger.error("Ошибка при загрузке файла", e);
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            // Обработка всех остальных исключений (например, проблемы с базой данных)
            logger.error("Неожиданная ошибка при загрузке файла", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    public List<File> getFilesByUser(Users users) {
        return fileRepository.findByUser(users);
    }

    @Transactional
    public File getFileById(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Transactional
    public CompletableFuture<Void> deleteFile(Long id) {
        File file = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
        Path filePath = Paths.get(file.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
        fileRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }
}