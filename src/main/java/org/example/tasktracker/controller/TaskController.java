package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Task;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.TaskRepository;
import org.example.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

/**
 * Контроллер для управления задачами.
 */
@Controller
public class TaskController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ColumnRepository columnRepository;
    @Autowired private BoardRepository boardRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/board/{boardId}/task/new")
    public String newTaskForm(@PathVariable("boardId") Long boardId, @RequestParam(value = "columnId") Long columnId, Model model) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Task task = new Task();

        if (columnId != null) {
            BoardColumn column = columnRepository.findById(columnId).orElseThrow();
            task.setColumn(column);
        }

        model.addAttribute("board", board);
        model.addAttribute("task", task);
        model.addAttribute("columns", columnRepository.findByBoardOrderByOrderAsc(board));

        return "task_form";
    }

    @PostMapping("/board/{boardId}/task/new")
    public String saveTask(@PathVariable("boardId") Long boardId, @RequestParam("title") String title, @RequestParam(value = "description") String description, @RequestParam("columnId") Long columnId, @RequestParam(value = "dueDate") String dueDate, Authentication authentication) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        BoardColumn column = columnRepository.findById(columnId).orElseThrow();
        User currentUser = userRepository.findByUsername(authentication.getName()).orElseThrow();

        Task task = new Task(title, description, currentUser, column);

        if (dueDate != null && !dueDate.isEmpty()) {
            task.setDueDate(LocalDate.parse(dueDate).atStartOfDay());
        }

        taskRepository.save(task);

        return "redirect:/dashboard/board/" + boardId;
    }

    @GetMapping("/task/{id}/edit")
    public String editTaskForm(@PathVariable("id") Long id, Model model) {
        Task task = taskRepository.findById(id).orElseThrow();
        Board board = task.getColumn().getBoard();

        model.addAttribute("board", board);
        model.addAttribute("task", task);
        model.addAttribute("columns", columnRepository.findByBoardOrderByOrderAsc(board));

        return "task_form";
    }

    @PostMapping("/task/{id}/edit")
    public String updateTask(@PathVariable("id") Long id, @RequestParam("title") String title, @RequestParam(value = "description") String description, @RequestParam("columnId") Long columnId, @RequestParam(value = "dueDate") String dueDate) {
        Task task = taskRepository.findById(id).orElseThrow();
        BoardColumn column = columnRepository.findById(columnId).orElseThrow();

        task.setTitle(title);
        task.setDescription(description);
        task.setColumn(column);

        // Обновляем дедлайн: парсим дату, если она есть, или очищаем поле
        if (dueDate != null && !dueDate.isEmpty()) {
            task.setDueDate(LocalDate.parse(dueDate).atStartOfDay());
        } else {
            task.setDueDate(null);
        }

        taskRepository.save(task);

        return "redirect:/dashboard/board/" + task.getColumn().getBoard().getId();
    }

    @PostMapping("/task/{id}/delete")
    public String deleteTask(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        Long boardId = task.getColumn().getBoard().getId();

        taskRepository.delete(task);

        return "redirect:/dashboard/board/" + boardId;
    }
}