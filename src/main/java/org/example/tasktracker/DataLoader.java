package org.example.tasktracker;

import org.example.tasktracker.entity.Board;
import org.example.tasktracker.entity.BoardColumn;
import org.example.tasktracker.entity.User;
import org.example.tasktracker.entity.UserBoard;
import org.example.tasktracker.repository.BoardRepository;
import org.example.tasktracker.repository.ColumnRepository;
import org.example.tasktracker.repository.UserBoardRepository;
import org.example.tasktracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final UserBoardRepository userBoardRepository;
    private final ColumnRepository columnRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, BoardRepository boardRepository,
                      UserBoardRepository userBoardRepository, ColumnRepository columnRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.userBoardRepository = userBoardRepository;
        this.columnRepository = columnRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Создание админа
        User admin = new User("admin", passwordEncoder.encode("admin"), "admin@test.com", "admin");
        userRepository.save(admin);

        // Создание обычного пользователя
        User user = new User("user", passwordEncoder.encode("user"), "user@test.com", "user");
        userRepository.save(user);

        // Доска админа с тремя колонками
        Board board1 = new Board("Доска Админа");
        boardRepository.save(board1);
        userBoardRepository.save(new UserBoard(admin, board1, "owner"));
        columnRepository.save(new BoardColumn("To Do", 1, board1));
        columnRepository.save(new BoardColumn("Doing", 2, board1));
        columnRepository.save(new BoardColumn("Done", 3, board1));

        // Доска пользователя с тремя колонками
        Board board2 = new Board("Доска Юзера");
        boardRepository.save(board2);
        userBoardRepository.save(new UserBoard(user, board2, "owner"));
        columnRepository.save(new BoardColumn("To Do", 1, board2));
        columnRepository.save(new BoardColumn("Doing", 2, board2));
        columnRepository.save(new BoardColumn("Done", 3, board2));
    }
}