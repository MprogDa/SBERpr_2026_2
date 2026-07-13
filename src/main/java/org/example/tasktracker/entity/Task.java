package org.example.tasktracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность Task задача внутри колонки
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false)
    private String title;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime dueDate;

    @jakarta.persistence.Column(nullable = false)
    private LocalDateTime dateCreate;

    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "id_column", nullable = false)
    private BoardColumn column;

    public Task() {
        this.dateCreate = LocalDateTime.now();
    }

    public Task(String title, String description, User author, BoardColumn column) {
        this();
        this.title = title;
        this.description = description;
        this.author = author;
        this.column = column;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getDateCreate() { return dateCreate; }
    public void setDateCreate(LocalDateTime dateCreate) { this.dateCreate = dateCreate; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public BoardColumn getColumn() { return column; }
    public void setColumn(BoardColumn column) { this.column = column; }
}