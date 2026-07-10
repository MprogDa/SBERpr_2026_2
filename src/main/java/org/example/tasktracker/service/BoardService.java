package org.example.tasktracker.service;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.entity.UserBoard;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.UserBoardRepository;
import org.example.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired private BoardRepository boardRepository;
    @Autowired private UserBoardRepository userBoardRepository;
    @Autowired private UserRepository userRepository;

    // Создание доски и привязка пользователя как владельца
    public Board CreateBoardWithTitle(String title, String username) {
        User currentUser = userRepository.findByUsername(username).orElseThrow();
        Board newBoard = new Board(title);
        boardRepository.save(newBoard);
        UserBoard userBoard = new UserBoard(currentUser, newBoard, "owner");
        userBoardRepository.save(userBoard);
        return newBoard;
    }
}