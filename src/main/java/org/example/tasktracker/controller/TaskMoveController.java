package org.example.tasktracker.controller;

import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Task;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.TaskRepository;
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

    @PostMapping("/task/{id}/move")
    public String moveTask(@PathVariable("id") Long id, @RequestParam("columnId") Long columnId)
    {

        Task task = taskRepository.findById(id).orElseThrow();
        BoardColumn newColumn = columnRepository.findById(columnId).orElseThrow();
        task.setColumn(newColumn);
        taskRepository.save(task);

        return "redirect:/dashboard/board/" + task.getColumn().getBoard().getId();
    }
}