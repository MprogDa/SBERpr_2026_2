package org.example.tasktracker.controller;

import jakarta.servlet.http.HttpSession;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Ключ для хранения email в сессии при восстановлении пароля
    private static final String SESSION_RECOVERY_EMAIL = "recoveryEmail";

    // Главная страница
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Страница входа
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Форма регистрации
    @GetMapping("/register")
    public String registerForm(Model model) {
        // Инициализируем пустые значения, если их нет в модели
        if (!model.containsAttribute("username")) model.addAttribute("username", "");
        if (!model.containsAttribute("email")) model.addAttribute("email", "");
        return "register_form";
    }

    // Обработка регистрации
    @PostMapping("/register")
    public String registerSubmit(@RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword, Model model) {

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают. Введите одинаковые значения в оба поля.");
            return "register_form";
        }

        if (password.isEmpty()) {
            model.addAttribute("error", "Пароль не может быть пустым.");
            return "register_form";
        }

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Пользователь с таким именем уже существует.");
            return "register_form";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Пользователь с таким email уже зарегистрирован.");
            return "register_form";
        }

        User newUser = new User(username, passwordEncoder.encode(password), email, "user");
        userRepository.save(newUser);

        return "redirect:/register/code";
    }

    // Страница подтверждения email
    @GetMapping("/register/code")
    public String registerCode() {
        return "register_code";
    }

    // Обработка подтверждения email
    @PostMapping("/register/code")
    public String registerCodeSubmit() {
        return "redirect:/login";
    }

    // Страница ввода email для восстановления пароля
    @GetMapping("/recovery")
    public String recoveryEmail() {
        return "recovery_email";
    }

    // Обработка ввода email
    @PostMapping("/recovery")
    public String recoveryEmailSubmit(@RequestParam("email") String email, HttpSession session) {
        if (email == null || email.isEmpty() || !userRepository.findByEmail(email).isPresent()) {
            return "redirect:/recovery?error";
        }
        session.setAttribute(SESSION_RECOVERY_EMAIL, email);
        return "redirect:/recovery/code";
    }

    // Страница ввода кода подтверждения
    @GetMapping("/recovery/code")
    public String recoveryCode(HttpSession session) {
        if (session.getAttribute(SESSION_RECOVERY_EMAIL) == null) {
            return "redirect:/recovery";
        }
        return "recovery_code";
    }

    // Обработка ввода кода
    @PostMapping("/recovery/code")
    public String recoveryCodeSubmit(HttpSession session) {
        if (session.getAttribute(SESSION_RECOVERY_EMAIL) == null) {
            return "redirect:/recovery";
        }
        return "redirect:/recovery/newpass";
    }

    // Страница ввода нового пароля
    @GetMapping("/recovery/newpass")
    public String recoveryNewPass(HttpSession session) {
        if (session.getAttribute(SESSION_RECOVERY_EMAIL) == null) {
            return "redirect:/recovery";
        }
        return "recovery_newpass";
    }

    // Обработка ввода нового пароля
    @PostMapping("/recovery/newpass")
    public String recoveryNewPassSubmit(@RequestParam("newPassword") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute(SESSION_RECOVERY_EMAIL);

        if (email == null || newPassword == null || newPassword.isEmpty()) {
            return "redirect:/recovery";
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/recovery";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        session.removeAttribute(SESSION_RECOVERY_EMAIL);

        return "redirect:/login?passwordChanged";
    }
}