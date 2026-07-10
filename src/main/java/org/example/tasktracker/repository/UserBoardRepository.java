package org.example.tasktracker.repository;

import org.example.tasktracker.entity.UserBoard;
import org.example.tasktracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {
    List<UserBoard> findByUser(User user);
}