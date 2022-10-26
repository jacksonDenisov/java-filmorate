package ru.yandex.practicum.filmorate.model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    private User user;

    @Test
    public void createUserTest() {
        user = new User(1, "test@mail.ru", "Логин", "Имя", LocalDate.of(1895, 12, 28));
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals("Логин", user.getLogin());
        assertEquals("Имя", user.getName());
        assertEquals(LocalDate.of(1895, 12, 28), user.getBirthday());
    }
}
