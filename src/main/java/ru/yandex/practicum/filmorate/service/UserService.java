package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
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
            log.info("Добавляем пользователя " + id + " в друзья к " + friendId);
            storage.addFriend(id, friendId);
        } else {
            log.info("Пользователь не найден!");
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    public void removeFriend(long id, long friendId) {
        log.info("Удаляем друга с id " + friendId + " у пользователя " + id);
        storage.removeFriend(id, friendId);
    }

    public List<User> findFriendsOfUser(long id) {
        log.info("Возвращаем список друзей для пользователя " + id);
        return storage.findFriendsOfUser(id);
    }

    public List<User> findCommonFriends(long id, long otherId) {
        log.info("Возвращаем список общих друзей у пользователей " + id + " и " + otherId);
        return storage.findCommonFriends(id, otherId);
    }

}
