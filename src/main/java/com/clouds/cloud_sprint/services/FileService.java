package com.clouds.cloud_sprint.services;

import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public File addFile(MultipartFile file, Users users) throws IOException {
        File newFile = new File();
        newFile.setFileName(file.getOriginalFilename());
        newFile.setContentType(file.getContentType());
        newFile.setFileSize(file.getSize());
        newFile.setFileData(file.getBytes());
        newFile.setUser(users);
        return fileRepository.save(newFile);
    }

    public List<File> getFilesByUser(Users users) {
        return fileRepository.findByUser(users);
    }

    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
}
