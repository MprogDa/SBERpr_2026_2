package org.example.tasktracker.repository;

import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Поиск задач колонки
    List<Task> findByColumn(BoardColumn column);
}