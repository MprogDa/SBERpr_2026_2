package org.example.tasktracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users_boards")
public class UserBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_board", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String userRights; // "owner", "editor", "viewer"

    public UserBoard() {}

    public UserBoard(User user, Board board, String userRights) {
        this.user = user;
        this.board = board;
        this.userRights = userRights;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
    public String getUserRights() { return userRights; }
    public void setUserRights(String userRights) { this.userRights = userRights; }
}