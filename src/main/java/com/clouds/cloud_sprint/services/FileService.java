package com.clouds.cloud_sprint.services;

import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Async
    @Transactional
    public CompletableFuture<File> addFile(MultipartFile file, Users users) throws IOException {
        String userFolder = users.getBaseFolderPath();
        Path path = Paths.get(userFolder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path filePath = Paths.get(userFolder, file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        File newFile = new File();
        newFile.setFileName(file.getOriginalFilename());
        newFile.setContentType(file.getContentType());
        newFile.setFileSize(file.getSize());
        newFile.setFilePath(filePath.toString());
        newFile.setUser(users);

        return CompletableFuture.completedFuture(fileRepository.save(newFile));
    }

    @Transactional
    public List<File> getFilesByUser(Users users) {
        return fileRepository.findByUser(users);
    }

    @Transactional
    public File getFileById(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Async
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