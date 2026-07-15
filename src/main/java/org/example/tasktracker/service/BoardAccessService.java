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
public class BoardAccessService {

    @Autowired private UserBoardRepository userBoardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BoardRepository boardRepository;

    // Возвращает true, если у пользователя есть одно из разрешенных прав
    public boolean hasAccess(Long boardId, String username, String... allowedRights) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Board board = boardRepository.findById(boardId).orElseThrow();

        return userBoardRepository.findByUserAndBoard(user, board)
                .map(ub -> {
                    for (String right : allowedRights) {
                        if (right.equals(ub.getUserRights())) return true;
                    }
                    return false;
                }).orElse(false);
    }
}