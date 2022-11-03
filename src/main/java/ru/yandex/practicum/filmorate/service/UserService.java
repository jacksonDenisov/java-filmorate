package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public List<User> findAll() {
        log.info("Возвращаем список всех пользователей.");
        return storage.findAll();
    }

    public User findById(Long id) {
        log.info("Возвращаем пользователя с id " + id);
        return storage.findById(id);
    }

    public User create(User user) {
        log.info("Создаем нового пользователя " + user);
        return storage.create(user);
    }

    public User update(User user) {
        log.info("Обновляем пользователя " + user);
        return storage.update(user);
    }

    public void addFriend(long id, long friendId) {
        if (storage.isIdExist(id) && storage.isIdExist(friendId)) {
            storage.findById(id).getFriends().add(friendId);
            storage.findById(friendId).getFriends().add(id);
            log.info("Пользователь " + id + " добавлен в друзья к " + friendId);
            log.info("Список друзей для пользователя " + id + " : " + storage.findById(id).getFriends());
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void removeFriend(long id, long friendId) {
        if (!storage.findById(id).getFriends().contains(friendId)) {
            log.info("Такого пользователя нет в друзьях");
            throw new NotFoundException("Такого пользователя нет в друзьях");
        }
        storage.findById(id).getFriends().remove(friendId);
        storage.findById(friendId).getFriends().remove(id);
        log.info("Пользователь " + id + " удален из друзей у " + friendId);
    }

    public List<User> findFriendsOfUser(long id) {
        log.info("Возвращаем список друзей для пользователя " + id);
        return storage.findAll()
                .stream()
                .filter(x -> storage.findById(id).getFriends().contains(x.getId()))
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(long id, long otherId) {
        Set<Long> CommonFriendsIds = storage.findById(id).getFriends()
                .stream()
                .filter(storage.findById(otherId).getFriends()::contains)
                .collect(Collectors.toSet());
        log.info("Возвращаем список общих друзей у пользователей " + id + " и " + otherId + " : " + CommonFriendsIds);
        return storage.findAll()
                .stream()
                .filter(x -> CommonFriendsIds.contains(x.getId()))
                .collect(Collectors.toList());
    }

}
