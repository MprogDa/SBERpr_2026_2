package org.example.tasktracker.controller;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для управления колонками на доске
 */
@Controller
public class ColumnController {

    @Autowired private ColumnRepository columnRepository;
    @Autowired private BoardRepository boardRepository;
    @Autowired private TaskRepository taskRepository;

    @PostMapping("/board/{boardId}/column/new")
    public String addColumn(@PathVariable("boardId") Long boardId,
                            @RequestParam("title") String title) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        int newOrder = columnRepository.findByBoardOrderByOrderAsc(board).size() + 1;
        BoardColumn newColumn = new BoardColumn(title, newOrder, board);
        columnRepository.save(newColumn);

        return "redirect:/dashboard/board/" + boardId;
    }

    @PostMapping("/column/{id}/edit")
    public String editColumn(@PathVariable("id") Long id,
                             @RequestParam("title") String title) {
        BoardColumn column = columnRepository.findById(id).orElseThrow();
        column.setTitle(title);
        columnRepository.save(column);

        return "redirect:/dashboard/board/" + column.getBoard().getId();
    }

    @PostMapping("/column/{id}/delete")
    public String deleteColumn(@PathVariable("id") Long id) {
        BoardColumn column = columnRepository.findById(id).orElseThrow();
        Long boardId = column.getBoard().getId();
        taskRepository.deleteAll(taskRepository.findByColumn(column));
        columnRepository.delete(column);

        return "redirect:/dashboard/board/" + boardId;
    }
}