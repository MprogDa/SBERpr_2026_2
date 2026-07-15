package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Task;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.entity.UserBoard;
import org.example.tasktracker.repository.*;
import org.example.tasktracker.service.BoardAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления настройками рабочего пространства (доски).
 */
@Controller
@RequestMapping("/dashboard/board/{id}")
public class BoardSettingsController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserBoardRepository userBoardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardAccessService accessService;

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Отображает страницу настроек доски (цвет и список участников).
     * Доступно только владельцам и редакторам.
     */
    @GetMapping("/settings")
    public String settings(@PathVariable("id") Long id, Authentication authentication, Model model) {
        // Проверка прав: доступ разрешен только владельцу или редактору
        if (!accessService.hasAccess(id, authentication.getName(), "owner", "editor")) {
            return "redirect:/dashboard/board/" + id;
        }

        Board board = boardRepository.findById(id).orElseThrow();
        List<UserBoard> members = userBoardRepository.findByBoard(board);

        model.addAttribute("board", board);
        model.addAttribute("members", members);
        return "board_settings";
    }

    /**
     * Сохраняет новый цвет доски.
     */
    @PostMapping("/settings/color") // Уточнен маршрут для ясности
    public String saveColor(@PathVariable("id") Long id, @RequestParam("color") String color) {
        Board board = boardRepository.findById(id).orElseThrow();
        board.setColor(color);
        boardRepository.save(board);
        return "redirect:/dashboard/board/" + id + "/settings";
    }

    /**
     * Добавляет нового участника на доску по его email.
     */
    @PostMapping("/settings/add")
    public String addMember(@PathVariable("id") Long id,
                            @RequestParam("email") String email,
                            @RequestParam("rights") String rights) {
        Board board = boardRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/dashboard/board/" + id + "/settings?error=notfound";
        }

        if (userBoardRepository.findByUserAndBoard(user, board).isPresent()) {
            return "redirect:/dashboard/board/" + id + "/settings?error=exists";
        }

        userBoardRepository.save(new UserBoard(user, board, rights));
        return "redirect:/dashboard/board/" + id + "/settings";
    }

    /**
     * Изменяет права доступа существующего участника.
     * Запрещает изменение прав владельца доски.
     */
    @PostMapping("/settings/{ubId}/rights")
    public String changeRights(@PathVariable("id") Long id,
                               @PathVariable("ubId") Long ubId,
                               @RequestParam("rights") String rights) {
        UserBoard ub = userBoardRepository.findById(ubId).orElseThrow();

        if (!"owner".equals(ub.getUserRights())) {
            ub.setUserRights(rights);
            userBoardRepository.save(ub);
        }
        return "redirect:/dashboard/board/" + id + "/settings";
    }

    /**
     * Удаляет участника из доски.
     * Запрещает удаление владельца доски.
     */
    @PostMapping("/settings/{ubId}/remove")
    public String removeMember(@PathVariable("id") Long id,
                               @PathVariable("ubId") Long ubId) {
        UserBoard ub = userBoardRepository.findById(ubId).orElseThrow();

        // Защита: владелец не может быть удален из доски (он должен сначала передать права или удалить доску)
        if (!"owner".equals(ub.getUserRights())) {
            userBoardRepository.delete(ub);
        }
        return "redirect:/dashboard/board/" + id + "/settings";
    }

    /**
     * Полностью удаляет доску и все связанные с ней данные.
     * Требует прав владельца.
     */
    @Transactional
    @PostMapping("/delete")
    public String deleteBoard(@PathVariable("id") Long id, Authentication authentication) {
        // Добавлена проверка безопасности: удалять доску может только владелец
        if (!accessService.hasAccess(id, authentication.getName(), "owner")) {
            return "redirect:/dashboard";
        }

        Board board = boardRepository.findById(id).orElseThrow();

        List<BoardColumn> columns = columnRepository.findByBoardOrderByOrderAsc(board);

        // Удаляем комментарии и задачи в каждой колонке
        for (BoardColumn col : columns) {
            List<Task> tasks = taskRepository.findByColumn(col);
            for (Task task : tasks) {
                commentRepository.deleteByTask(task);
            }
            taskRepository.deleteAll(tasks);
        }

        // Удаляем колонки
        columnRepository.deleteAll(columns);

        // Удаляем записи о правах участников этой доски
        userBoardRepository.deleteAll(userBoardRepository.findByBoard(board));

        // Удаляем саму доску
        boardRepository.delete(board);

        return "redirect:/dashboard";
    }
}