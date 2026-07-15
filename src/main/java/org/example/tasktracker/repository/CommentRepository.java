package org.example.tasktracker.repository;

import org.example.tasktracker.entity.Comment;
import org.example.tasktracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Получение комментариев для отображения
    List<Comment> findByTaskOrderByDateCreateAsc(Task task);

    // Удаление всех комментариев, привязанных к конкретной задаче
    void deleteByTask(Task task);
}