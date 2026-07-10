package org.example.tasktracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность User - пользователь системы.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // user или admin
    @Column(nullable = false)
    private String role;

    // true активен, false заблокирован
    @Column(nullable = false)
    private boolean isActive = true;

    private String recoveryHash;

    private LocalDateTime recoveryHashExpiration;

    public User() {}

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getRecoveryHash() { return recoveryHash; }
    public void setRecoveryHash(String recoveryHash) { this.recoveryHash = recoveryHash; }
    public LocalDateTime getRecoveryHashExpiration() { return recoveryHashExpiration; }
    public void setRecoveryHashExpiration(LocalDateTime recoveryHashExpiration) { this.recoveryHashExpiration = recoveryHashExpiration; }
}