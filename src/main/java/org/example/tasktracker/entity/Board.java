package org.example.tasktracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность Board рабочая доска
 */
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime dateCreate;

    public Board() {
        this.dateCreate = LocalDateTime.now();
    }

    public Board(String title) {
        this();
        this.title = title;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDateCreate() { return dateCreate; }
    public void setDateCreate(LocalDateTime dateCreate) { this.dateCreate = dateCreate; }
}