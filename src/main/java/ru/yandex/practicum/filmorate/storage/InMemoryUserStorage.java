package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private static long id = 0;


    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User findById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public User create(User user) {
        if (users.containsKey(user.getId())) {
            throw new UserValidationException("Такой пользователь уже существует!");
        } else if (user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин пользователя не прошел валидацию!");
        } else if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(id, user);
        log.info("Пользователь {} успешно добавлен", user.getName());
        return user;
    }


    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Такого пользователя не существует!");
        } else if (user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин пользователя не прошел валидацию!");
        } else if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно обновлен", user.getName());
        return user;
    }

    public boolean isIdExist(long id) {
        return users.containsKey(id);
    }

}
