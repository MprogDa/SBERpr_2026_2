package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Task;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.TaskRepository;
import org.example.tasktracker.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления досками (рабочими пространствами)
 */
@Controller
public class BoardController {

    @Autowired private BoardService boardService;
    @Autowired private BoardRepository boardRepository;
    @Autowired private ColumnRepository columnRepository;
    @Autowired private TaskRepository taskRepository;

    @GetMapping("/board/new")
    public String newBoardForm() {
        return "board_create";
    }

    @PostMapping("/board/new")
    public String createBoard(@RequestParam("title") String title, Authentication authentication) {
        boardService.createBoardWithTitle(title, authentication.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard/board/{id}")
    public String viewBoard(@PathVariable("id") Long id, Model model) {
        Board board = boardRepository.findById(id).orElseThrow();

        List<BoardColumn> columns = columnRepository.findByBoardOrderByOrderAsc(board);

        Map<Long, List<Task>> tasksByColumn = new HashMap<>();
        for (BoardColumn column : columns) {
            tasksByColumn.put(column.getId(), taskRepository.findByColumn(column));
        }

        model.addAttribute("board", board);
        model.addAttribute("columns", columns);
        model.addAttribute("tasksByColumn", tasksByColumn);

        return "board_view";
    }
}