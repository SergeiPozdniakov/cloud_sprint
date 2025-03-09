package com.clouds.cloud_sprint.controller;

import com.clouds.cloud_sprint.model.File;
import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.security.UserDetailsServiceImpl;
import com.clouds.cloud_sprint.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private FileService fileService;

    @GetMapping
    public String home(@AuthenticationPrincipal Users users, Model model) {
        if (users == null) {
            return "redirect:/login";
        }
        logger.info("Пользователь {} аутентифицирован, переход на home.html", users.getUsername());

        List<File> files = fileService.getFilesByUser(users);
        model.addAttribute("files", files);
        return "home"; // Должен совпадать с именем файла home.html
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Users users, Model model) throws IOException {
        fileService.addFile(file, users);
        return "redirect:/home";
    }

    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return "redirect:/home";
    }
}
