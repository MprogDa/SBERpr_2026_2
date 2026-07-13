package org.example.tasktracker.repository;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ColumnRepository extends JpaRepository<BoardColumn, Long> {
    // Поиск колонок доски, отсортированных по порядку
    List<BoardColumn> findByBoardOrderByOrderAsc(Board board);
}