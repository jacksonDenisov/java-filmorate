package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.LikedByStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenresStorage genresStorage;
    private final LikedByStorage likedByStorage;
    private final MpaStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;


    User user1;
    User user2;

    @BeforeEach
    public void setup() {
        user1 = new User(1, "Имя", "test@mail.ru", "Логин",
                LocalDate.of(1895, 12, 28), new HashSet<>());
        user2 = new User(2, "Имя2", "test2@mail.ru", "Логин2",
                LocalDate.of(1894, 12, 28), new HashSet<>());
    }

    @AfterEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM liked_by");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }


    @Test
    public void createAndFindUserTest() {
        userStorage.create(user1);
        assertEquals(userStorage.findById(1).getName(), "Имя");
    }

    @Test
    public void FindAllUsersTest() {
        userStorage.create(user1);
        userStorage.create(user2);
        assertEquals(userStorage.findAll().size(), 2);
    }

    @Test
    public void updateUserTest() {
        userStorage.create(user1);
        user1.setName("newName");
        userStorage.update(user1);
        assertEquals(userStorage.findById(1).getName(), "newName");
    }



}
