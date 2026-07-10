package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.entity.UserBoard;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.UserBoardRepository;
import org.example.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired private UserRepository userRepository;
    @Autowired private BoardRepository boardRepository;
    @Autowired private UserBoardRepository userBoardRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // список досок
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");

        List<Board> boardsToShow = new ArrayList<>();
        if (isAdmin) {
            boardsToShow = boardRepository.findAll();
        } else {
            List<UserBoard> userBoards = userBoardRepository.findByUser(currentUser);
            for (UserBoard ub : userBoards) {
                boardsToShow.add(ub.getBoard());
            }
        }

        model.addAttribute("boards", boardsToShow);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUser", currentUser);
        return "dashboard";
    }

    // Свой профиль
    @GetMapping("/dashboard/profile")
    public String profile(Authentication authentication, Model model) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("user", currentUser);
        model.addAttribute("isAdmin", currentUser.getRole().equalsIgnoreCase("admin"));
        model.addAttribute("isOwnProfile", true);
        return "profile";
    }

    // Профиль другого пользователя (только для админа)
    @GetMapping("/dashboard/users/{id}/profile")
    public String viewUserProfile(@PathVariable("id") Long id, Authentication authentication, Model model) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/dashboard";
        }

        User targetUser = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", targetUser);
        model.addAttribute("isAdmin", true);
        model.addAttribute("isOwnProfile", currentUser.getId().equals(targetUser.getId()));
        return "profile";
    }

    // Сохранение своего профиля
    @PostMapping("/dashboard/profile")
    public String profileUpdate(@RequestParam("id") Long id,
                                @RequestParam("username") String username,
                                @RequestParam("email") String email,
                                @RequestParam(value = "oldPassword") String oldPassword,
                                @RequestParam(value = "newPassword") String newPassword,
                                @RequestParam(value = "role") String role,
                                Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        if (!currentUser.getId().equals(id)) {
            return "redirect:/dashboard/profile";
        }

        boolean usernameChanged = !currentUser.getUsername().equals(username);

        currentUser.setUsername(username);
        currentUser.setEmail(email);

        if (role != null && !role.isEmpty()) {
            currentUser.setRole(role);
        }

        // Смена пароля с проверкой старого
        if (newPassword != null && !newPassword.isEmpty()) {
            if (oldPassword == null || oldPassword.isEmpty() ||
                    !passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                return "redirect:/dashboard/profile?wrongPassword";
            }
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(currentUser);

        if (usernameChanged) {
            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            currentUser.getPassword(),
                            authentication.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/dashboard/profile";
    }

    // Сохранение профиля другого пользователя (только для админа)
    @PostMapping("/dashboard/users/{id}/profile")
    public String updateUserProfile(@PathVariable("id") Long id,
                                    @RequestParam("username") String username,
                                    @RequestParam("email") String email,
                                    @RequestParam(value = "role") String role,
                                    @RequestParam(value = "newPassword") String newPassword,
                                    Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/dashboard/profile";
        }

        User targetUser = userRepository.findById(id).orElseThrow();
        targetUser.setUsername(username);
        targetUser.setEmail(email);

        if (role != null && !role.isEmpty()) {
            targetUser.setRole(role);
        }

        // Админ может менять пароль без проверки старого
        if (newPassword != null && !newPassword.isEmpty()) {
            targetUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(targetUser);

        return "redirect:/dashboard/users/" + id + "/profile";
    }

    // Переключение активности пользователя (только для админа)
    @PostMapping("/dashboard/users/{id}/toggleActive")
    public String toggleUserActive(@PathVariable("id") Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/dashboard";
        }

        User targetUser = userRepository.findById(id).orElseThrow();
        targetUser.setActive(!targetUser.isActive());
        userRepository.save(targetUser);

        return "redirect:/dashboard/users/" + id + "/profile";
    }

    // Деактивация аккаунта
    @PostMapping("/dashboard/profile/delete")
    public String deleteOwnAccount(Authentication authentication, HttpSession session) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        currentUser.setActive(false);
        userRepository.save(currentUser);
        SecurityContextHolder.clearContext();
        session.invalidate();

        return "redirect:/?deleted";
    }

    // Список пользователей (только для админа)
    @GetMapping("/dashboard/users")
    public String users(Authentication authentication, Model model) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) return "redirect:/login";

        if (!currentUser.getRole().equalsIgnoreCase("admin")) {
            return "redirect:/dashboard";
        }
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    // Просмотр доски
    @GetMapping("/dashboard/board/{id}")
    public String viewBoard(@PathVariable("id") Long id, Model model) {
        Board board = boardRepository.findById(id).orElseThrow();
        model.addAttribute("board", board);
        return "board_view";
    }

    // Получение текущего пользователя из сессии
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}