package org.example.tasktracker.controller;

import org.example.tasktracker.entity.*;
import org.example.tasktracker.repository.*;
import org.example.tasktracker.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления рабочими пространствами (досками).
 */
@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserBoardRepository userBoardRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Отображает форму создания новой доски.
     */
    @GetMapping("/new")
    public String newBoardForm() {
        return "board_create";
    }

    /**
     * Обрабатывает запрос на создание новой доски.
     */
    @PostMapping("/new")
    public String createBoard(@RequestParam("title") String title, Authentication authentication) {
        // Делегирование бизнес-логики сервису
        boardService.createBoardWithTitle(title, authentication.getName());
        return "redirect:/dashboard";
    }

    /**
     * Отображает просмотр доски с её колонками и задачами.
     */
    @GetMapping("/dashboard/{id}")
    public String viewBoard(@PathVariable("id") Long id, Authentication authentication, Model model) {
        Board board = boardRepository.findById(id).orElseThrow();

        List<BoardColumn> columns = columnRepository.findByBoardOrderByOrderAsc(board);

        Map<Long, List<Task>> tasksByColumn = new HashMap<>();
        for (BoardColumn column : columns) {
            tasksByColumn.put(column.getId(), taskRepository.findByColumn(column));
        }

        User currentUser = userRepository.findByUsername(authentication.getName()).orElseThrow();
        String rights = userBoardRepository.findByUserAndBoard(currentUser, board)
                .map(UserBoard::getUserRights)
                .orElse("viewer"); // Права по умолчанию, если запись в связной таблице отсутствует

        model.addAttribute("board", board);
        model.addAttribute("columns", columns);
        model.addAttribute("tasksByColumn", tasksByColumn);
        model.addAttribute("userRights", rights);

        return "board_view";
    }
}