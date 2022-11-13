package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private UserStorage storage;
    private static User user1;
    private static User user2;

    @BeforeAll
    public static void createTestObjects() {
        user1 = new User(1, "test@mail.ru", "Логин", "Имя",
                LocalDate.of(1895, 12, 28), new HashSet<>());
        user2 = new User(2, "test2@mail.ru", "Логин2", "Имя2",
                LocalDate.of(1895, 12, 28), new HashSet<>());
    }

    @BeforeEach
    public void setup() {
        this.storage = new InMemoryUserStorage();
    }

    @Test
    public void findAllShouldReturnCorrectUserList() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(user1);
        assertEquals(storage.findAll().size(), 1);
    }

    @Test
    public void findByIdShouldReturnUserById() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(user1);
        assertEquals(user1, storage.findById(user1.getId()));
    }

    @Test
    public void findByIdShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> storage.findById(33L));
    }


    @Test
    public void createShouldCreateUser() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(user1);
        assertEquals(storage.findById(user1.getId()), user1);
    }

    @Test
    public void createShouldThrowUserValidationException() {
        storage.create(user1);
        assertThrows(UserValidationException.class, () -> storage.create(user1));
        user2.setLogin(" ");
        assertThrows(UserValidationException.class, () -> storage.create(user2));
    }

    @Test
    public void updateShouldUpdateUser() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(user1);
        user2.setId(user1.getId());
        storage.update(user2);
        assertEquals(storage.findById(user1.getId()), user2);
    }

    @Test
    public void updateShouldThrowUserValidationException() {
        storage.create(user1);
        user1.setLogin(" ");
        assertThrows(UserValidationException.class, () -> storage.update(user1));
        user1.setLogin("test");
    }

    @Test
    public void updateShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> storage.update(user1));
    }

    @Test
    public void isIdExistShouldReturnTrue() {
        storage.create(user1);
        assertTrue(storage.isIdExist(user1.getId()));
    }

}