package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private static int id = 0;

    @GetMapping
    protected ArrayList<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    protected ResponseEntity<User> create(@Valid @RequestBody User user) {
        try {
            if (users.containsKey(user.getId())) {
                log.warn("Такой пользователь уже существует!");
                return ResponseEntity.internalServerError().body(user);
            } else if (user.getLogin().contains(" ")) {
                throw new UserValidationException("Логин пользователя не прошел валидацию!");
            }  else if (user.getName() == null || user.getName().equals("")) {
                user.setName(user.getLogin());
            }
            user.setId(++id);
            users.put(id, user);
            log.info("Пользователь {} успешно добавлен", user.getName());
            return ResponseEntity.ok().body(user);
        } catch (UserValidationException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(user);
        }
    }

    @PutMapping
    protected ResponseEntity<User> update(@Valid @RequestBody User user) {
        try {
            if (!users.containsKey(user.getId())) {
                log.warn("Такого пользователя не существует!");
                return ResponseEntity.internalServerError().body(user);
            } else if (user.getLogin().contains(" ")) {
                throw new UserValidationException("Логин пользователя не прошел валидацию!");
            } else if (user.getName() == null || user.getName().equals("")) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь {} успешно обновлен", user.getName());
            return ResponseEntity.ok().body(user);
        } catch (UserValidationException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(user);
        }
    }
}