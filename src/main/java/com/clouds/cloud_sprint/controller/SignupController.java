package com.clouds.cloud_sprint.controller;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new Users()); // Добавляем пустой объект пользователя
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid Users user, BindingResult result, Model model) {
        // Проверка на существующего пользователя
        if (userService.getUserByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "signup";
        }

        // Проверка на ошибки валидации
        if (result.hasErrors()) {
            model.addAttribute("error", "Проверьте введенные данные");
            return "signup";
        }

        userService.createUser(user);
        model.addAttribute("message", "Регистрация успешна! Теперь вы можете войти.");
        return "redirect:/login"; // Используем редирект вместо прямого возврата страницы
    }
}
