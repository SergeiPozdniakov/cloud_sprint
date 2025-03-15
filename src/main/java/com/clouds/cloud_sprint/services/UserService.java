package com.clouds.cloud_sprint.services;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users createUser(Users users) {
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        String basePath = "C:\\cloud_storage";
        String userFolderPath = basePath + "/" + users.getUsername();
        users.setBaseFolderPath(userFolderPath);

        try {
            Path path = Paths.get(userFolderPath);
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create user folder", e);
        }

        return userRepository.save(users);
    }

    public Optional<Users> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
