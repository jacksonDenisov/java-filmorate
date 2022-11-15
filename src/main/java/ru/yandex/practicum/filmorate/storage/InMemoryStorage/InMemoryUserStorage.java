package ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private static long id = 0;


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    @Override
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


    @Override
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

    @Override
    public boolean isIdExist(long id) {
        return users.containsKey(id);
    }

    @Override
    public void addFriend(long id, long friendId) {
            findById(id).getFriends().add(friendId);
            findById(friendId).getFriends().add(id);
            log.info("Пользователь " + id + " добавлен в друзья к " + friendId);
            log.info("Список друзей для пользователя " + id + " : " + findById(id).getFriends());
    }

    @Override
    public void removeFriend(long id, long friendId) {
        if (!findById(id).getFriends().contains(friendId)) {
            log.info("Такого пользователя нет в друзьях");
            throw new NotFoundException("Такого пользователя нет в друзьях");
        }
        findById(id).getFriends().remove(friendId);
        findById(friendId).getFriends().remove(id);
        log.info("Пользователь " + id + " удален из друзей у " + friendId);
    }

    @Override
    public List<User> findFriendsOfUser(long id) {
        return findAll()
                .stream()
                .filter(x -> findById(id).getFriends().contains(x.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(long id, long otherId) {
        Set<Long> CommonFriendsIds = findById(id).getFriends()
                .stream()
                .filter(findById(otherId).getFriends()::contains)
                .collect(Collectors.toSet());
        return findAll()
                .stream()
                .filter(x -> CommonFriendsIds.contains(x.getId()))
                .collect(Collectors.toList());
    }

}
