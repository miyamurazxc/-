package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // Главная страница
    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная — Менеджер контактов");
        return "home";
    }

    // Страница "О проекте"
    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "О проекте — Менеджер контактов");
        return "about";
    }

    // Страница регистрации
    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Регистрация — Менеджер контактов");
        model.addAttribute("user", new User());
        return "signup";
    }

    // Обработчик регистрации пользователя
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result1,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {

        try {

            // Проверка: принял ли пользователь условия использования
            if (!agreement) {
                System.out.println("Вы не приняли условия использования.");
                throw new Exception("Вы не приняли условия использования.");
            }

            // Проверка ошибок валидации
            if (result1.hasErrors()) {
                System.out.println("Ошибка: " + result1.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            // Устанавливаем настройки нового пользователя
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement: " + agreement);
            System.out.println("USER: " + user);

            // Сохранение пользователя в базе данных
            User result = this.userRepository.save(user);

            // Обнуляем форму
            model.addAttribute("user", new User());

            // Успешное сообщение
            session.setAttribute("message",
                    new Message("Вы успешно зарегистрировались!", "alert-success"));
            return "signup";

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("user", user);

            // Сообщение об ошибке
            session.setAttribute("message",
                    new Message("Произошла ошибка: " + e.getMessage(), "alert-danger"));

            return "signup";
        }
    }

    // Страница входа (кастомная форма)
    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Вход — Менеджер контактов");
        return "login";
    }
}

