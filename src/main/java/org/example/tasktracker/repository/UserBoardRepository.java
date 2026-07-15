package org.example.tasktracker.repository;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.entity.UserBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {
    List<UserBoard> findByUser(User user);
    List<UserBoard> findByBoard(Board board);

    // Новый метод
    Optional<UserBoard> findByUserAndBoard(User user, Board board);
}