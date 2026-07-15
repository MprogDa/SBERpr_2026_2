package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.TaskRepository;
import org.example.tasktracker.service.BoardAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для управления колонками рабочих пространств (досок)
 */
@Controller
public class ColumnController {

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardAccessService accessService;

    /**
     * доска +
     * владельцам и редакторам
     */
    @PostMapping("/board/{boardId}/column/new")
    public String addColumn(@PathVariable("boardId") Long boardId,
                            @RequestParam("title") String title,
                            Authentication authentication) {
        if (!accessService.hasAccess(boardId, authentication.getName(), "owner", "editor")) {
            return "redirect:/dashboard/board/" + boardId;
        }

        Board board = boardRepository.findById(boardId).orElseThrow();

        int newOrder = columnRepository.findByBoardOrderByOrderAsc(board).size() + 1;

        BoardColumn newColumn = new BoardColumn(title, newOrder, board);
        columnRepository.save(newColumn);

        return "redirect:/dashboard/board/" + boardId;
    }

    /**
     * Редактирует название существующей колонки.
     * Доступно только владельцам и редакторам доски, которой принадлежит колонка.
     */
    @PostMapping("/column/{id}/edit")
    public String editColumn(@PathVariable("id") Long id,
                             @RequestParam("title") String title,
                             Authentication authentication) {
        BoardColumn column = columnRepository.findById(id).orElseThrow();
        Long boardId = column.getBoard().getId();

        if (!accessService.hasAccess(boardId, authentication.getName(), "owner", "editor")) {
            return "redirect:/dashboard/board/" + boardId;
        }

        column.setTitle(title);
        columnRepository.save(column);

        return "redirect:/dashboard/board/" + boardId;
    }

    /**
     * Удаляет колонку и все связанные с ней задачи
     */
    @PostMapping("/column/{id}/delete")
    public String deleteColumn(@PathVariable("id") Long id,
                               Authentication authentication) {
        BoardColumn column = columnRepository.findById(id).orElseThrow();
        Long boardId = column.getBoard().getId();

        if (!accessService.hasAccess(boardId, authentication.getName(), "owner", "editor")) {
            return "redirect:/dashboard/board/" + boardId;
        }

        taskRepository.deleteAll(taskRepository.findByColumn(column));

        columnRepository.delete(column);

        return "redirect:/dashboard/board/" + boardId;
    }
}