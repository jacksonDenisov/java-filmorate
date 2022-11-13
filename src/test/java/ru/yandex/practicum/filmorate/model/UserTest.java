package ru.yandex.practicum.filmorate.model;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    public void createUserTest() {
        User user = new User(1, "Имя", "test@mail.ru", "Логин",
                LocalDate.of(1895, 12, 28), new HashSet<>());
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals("Логин", user.getLogin());
        assertEquals("Имя", user.getName());
        assertEquals(LocalDate.of(1895, 12, 28), user.getBirthday());
        assertEquals(user.getFriends().size(), 0);
    }
}
