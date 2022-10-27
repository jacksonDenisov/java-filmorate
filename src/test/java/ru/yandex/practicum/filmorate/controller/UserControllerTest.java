package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserController controller;


    @BeforeEach
    public void setup() {
        this.controller = new UserController();
    }

    @Test
    public void createShouldReturnOkWhenUserIsCorrect() {
        User user = new User(1, "test@mail.ru", "Логин", "Имя", LocalDate.of(1895, 12, 28));
        assertEquals(ResponseEntity.ok().body(user), controller.create(user));
    }

    @Test
    public void createShouldReturnInternalServerErrorWhenUserIsAlreadyExist() {
        User user = new User(1, "test@mail.ru", "Логин", "Имя", LocalDate.of(1895, 12, 28));
        controller.create(user);
        assertEquals(ResponseEntity.internalServerError().body(user), controller.create(user));
    }

    @Test
    public void createShouldReturnBadRequestWhenFailUserLogin() {
        User user = new User(1, "test@mail.ru", "Логин с пробелом", "Имя", LocalDate.of(1895, 12, 28));
        assertEquals(ResponseEntity.badRequest().body(user), controller.create(user));
    }

    @Test
    public void createShouldReturnOkAndLoginAsNameWhenEmptyOrNullUserName() {
        User user = new User(1, "test@mail.ru", "login", "", LocalDate.of(1895, 12, 28));
        assertEquals(user.getName(), "");
        assertEquals(ResponseEntity.ok().body(user), controller.create(user));
        assertEquals(user.getName(), "login");
        user.setName(null);
        user.setId(2);
        assertEquals(ResponseEntity.ok().body(user), controller.create(user));
        assertEquals(user.getName(), "login");

    }


    @Test
    public void findAllShouldReturnArrayOfUsers() {
        User user = new User(1, "test@mail.ru", "Логин", "Имя", LocalDate.of(1895, 12, 28));
        controller.create(user);
        List<User> allUsers = controller.findAll();
        assertEquals(1, allUsers.size());
    }

    @Test
    public void updateShouldReturnOkWhenUserIsCorrect() {
        User user = new User(1, "test@mail.ru", "Логин", "Имя", LocalDate.of(1895, 12, 28));
        controller.create(user);
        user.setEmail("test2@mail.ru");
        assertEquals(ResponseEntity.ok().body(user), controller.update(user));
    }

    @Test
    public void updateShouldReturnBadRequestWhenFailUserLogin() {
        User user = new User(1, "test@mail.ru", "Логин", "Имя", LocalDate.of(1895, 12, 28));
        controller.create(user);
        user.setLogin("Логин с пробелом");
        assertEquals(ResponseEntity.badRequest().body(user), controller.update(user));
    }

    @Test
    public void updateShouldReturnOkAndLoginAsNameWhenEmptyOrNullUserName() {
        User user = new User(1, "test@mail.ru", "login", "Name", LocalDate.of(1895, 12, 28));
        controller.create(user);
        assertEquals(user.getName(), "Name");
        user.setName("");
        assertEquals(ResponseEntity.ok().body(user), controller.update(user));
        assertEquals(user.getName(), "login");
        user.setName(null);
        assertEquals(ResponseEntity.ok().body(user), controller.update(user));
        assertEquals(user.getName(), "login");
    }

}