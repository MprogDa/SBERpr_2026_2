package org.example.tasktracker.repository;

import org.example.tasktracker.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}