package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User findById(long id);

    User create(User user);

    User update(User user);

    boolean isIdExist(long id);

    void addFriend(long id, long friendId);

    void removeFriend(long id, long friendId);

    List<User> findFriendsOfUser(long id);

    List<User> findCommonFriends(long id, long otherId);

}
