package org.example.tasktracker.controller;

import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Task;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.TaskRepository;
import org.example.tasktracker.service.BoardAccessService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для управления перемещением задач
 */
@Controller
public class TaskMoveController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ColumnRepository columnRepository;
    @Autowired private BoardAccessService accessService;
    @Autowired private BoardRepository boardRepository;

    @PostMapping("/task/{id}/move")
    public String moveTask(@PathVariable("id") Long id, @RequestParam("columnId") Long columnId, Authentication authentication)
    {
        Task task = taskRepository.findById(id).orElseThrow();
        Long boardId = task.getColumn().getBoard().getId();

        if (!accessService.hasAccess(boardId, authentication.getName(), "owner", "editor")) {
            return "redirect:/dashboard/board/" + boardId;
        }
        BoardColumn newColumn = columnRepository.findById(columnId).orElseThrow();
        task.setColumn(newColumn);
        taskRepository.save(task);

        return "redirect:/dashboard/board/" + task.getColumn().getBoard().getId();
    }
}