package org.example.tasktracker.controller;

import org.example.tasktracker.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController {

    @Autowired private BoardService boardService;

    // Отображение формы создания доски
    @GetMapping("/board/new")
    public String newBoardForm() {
        return "board_create";
    }

    // Создание доски и привязка к текущему пользователю
    @PostMapping("/board/new")
    public String createBoard(@RequestParam("title") String title, Authentication authentication) {
        boardService.CreateBoardWithTitle(title, authentication.getName());
        return "redirect:/dashboard";
    }
}