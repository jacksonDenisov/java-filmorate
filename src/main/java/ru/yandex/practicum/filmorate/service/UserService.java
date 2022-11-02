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
        } else {
            storage.findById(id).getFriends().remove(friendId);
            storage.findById(friendId).getFriends().remove(id);
            log.info("Пользователь " + id + " удален из друзей у " + friendId);
        }
    }

    public List<User> findFriendsOfUser(long id) {
        List<User> friends = new ArrayList<>();
        for (long friendId : storage.findById(id).getFriends()) {
            friends.add(storage.findById(friendId));
        }
        log.info("Возвращаем список друзей для пользователя " + id + " : " + friends);
        return friends;
    }

    public List<User> findCommonFriends(long id, long otherId) {
        List<User> commonFriends = new ArrayList<>();
        Set<Long> first = storage.findById(id).getFriends();
        Set<Long> second = storage.findById(otherId).getFriends();
        Set<Long> result = first.stream().filter(second::contains).collect(Collectors.toSet());
        for (long tempId : result) {
            commonFriends.add(storage.findById(tempId));
        }
        log.info("Возвращаем список общих друзей у пользователей " + id + " и " + otherId + " : " + commonFriends);
        return commonFriends;
    }

}
