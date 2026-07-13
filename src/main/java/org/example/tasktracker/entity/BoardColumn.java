package org.example.tasktracker.entity;

import jakarta.persistence.*;

/**
 * Сущность колонка на доске
 */
@Entity
@Table(name = "columns")
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Используем полное имя аннотации, чтобы избежать конфликта имен с классами-сущностями
    @jakarta.persistence.Column(nullable = false)
    private String title;

    @jakarta.persistence.Column(name = "column_order", nullable = false)
    private int order;
    @ManyToOne
    @JoinColumn(name = "id_board", nullable = false)
    private Board board;

    public BoardColumn() {}

    public BoardColumn(String title, int order, Board board) {
        this.title = title;
        this.order = order;
        this.board = board;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
}