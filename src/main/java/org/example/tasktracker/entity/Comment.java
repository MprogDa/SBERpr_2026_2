package org.example.tasktracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность Comment комментарий к задаче
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime dateCreate;

    @ManyToOne
    @JoinColumn(name = "id_task", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User author;

    public Comment() {
        this.dateCreate = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getDateCreate() { return dateCreate; }
    public void setDateCreate(LocalDateTime dateCreate) { this.dateCreate = dateCreate; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
}