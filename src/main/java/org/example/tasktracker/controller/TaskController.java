package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Comment;
import org.example.tasktracker.entity.Task;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.CommentRepository;
import org.example.tasktracker.repository.TaskRepository;
import org.example.tasktracker.repository.UserRepository;
import org.example.tasktracker.service.BoardAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для управления задачами.
 */
@Controller
public class TaskController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ColumnRepository columnRepository;
    @Autowired private BoardRepository boardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BoardAccessService accessService;

    @Autowired private CommentRepository commentRepository;

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
    public String saveTask(@PathVariable("boardId") Long boardId,
                           @RequestParam("title") String title,
                           @RequestParam(value = "description") String description,
                           @RequestParam("columnId") Long columnId,
                           @RequestParam(value = "dueDate") String dueDate,
                           Authentication authentication) {
        if (!accessService.hasAccess(boardId, authentication.getName(), "owner", "editor")) {
            return "redirect:/dashboard/board/" + boardId;
        }
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
    public String editTaskForm(@PathVariable("id") Long id, Authentication authentication, Model model) {
        Task task = taskRepository.findById(id).orElseThrow();
        Board board = task.getColumn().getBoard();
        User currentUser = userRepository.findByUsername(authentication.getName()).orElseThrow();

        model.addAttribute("board", board);
        model.addAttribute("task", task);
        model.addAttribute("columns", columnRepository.findByBoardOrderByOrderAsc(board));

        model.addAttribute("comments", commentRepository.findByTaskOrderByDateCreateAsc(task));
        model.addAttribute("currentUser", currentUser);

        return "task_form";
    }

    @PostMapping("/task/{id}/edit")
    public String updateTask(@PathVariable("id") Long id,
                             @RequestParam("title") String title,
                             @RequestParam(value = "description") String description,
                             @RequestParam("columnId") Long columnId,
                             @RequestParam(value = "dueDate") String dueDate) {
        Task task = taskRepository.findById(id).orElseThrow();
        BoardColumn column = columnRepository.findById(columnId).orElseThrow();

        task.setTitle(title);
        task.setDescription(description);
        task.setColumn(column);

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

    @GetMapping("/task/{id}/comments")
    @ResponseBody
    public List<Map<String, Object>> getTaskComments(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        List<Comment> comments = commentRepository.findByTaskOrderByDateCreateAsc(task);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        return comments.stream().map(c -> {
            Map<String, Object> map = new HashMap<>(); // Исправлена опечатка (было "a new")
            map.put("author", c.getAuthor().getUsername());
            map.put("text", c.getText());
            map.put("date", c.getDateCreate().format(formatter));
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/task/{id}/comment")
    @ResponseBody
    public String addComment(@PathVariable("id") Long id,
                             @RequestParam("text") String text,
                             Authentication authentication) {
        Task task = taskRepository.findById(id).orElseThrow();
        User author = userRepository.findByUsername(authentication.getName()).orElseThrow();

        Comment comment = new Comment();
        comment.setText(text);
        comment.setDateCreate(java.time.LocalDateTime.now());
        comment.setTask(task);
        comment.setAuthor(author);

        commentRepository.save(comment);

        return "ok";
    }
}