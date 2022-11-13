package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage.InMemoryUserStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {

    private UserService service;
    private User user1;
    private User user2;


    @BeforeEach
    public void setup() {
        this.service = new UserService(new InMemoryUserStorage());
        user1 = new User(1, "test@mail.ru", "Логин", "Имя",
                LocalDate.of(1895, 12, 28), new HashSet<>());
        user2 = new User(2, "test2@mail.ru", "Логин2", "Имя2",
                LocalDate.of(1895, 12, 28), new HashSet<>());
    }

    @Test
    public void addFriendShouldAddFriends() {
        service.create(user1);
        service.create(user2);
        assertEquals(service.findById(user1.getId()).getFriends().size(), 0);
        assertEquals(service.findById(user2.getId()).getFriends().size(), 0);
        service.addFriend(user1.getId(), user2.getId());
        assertEquals(service.findById(user1.getId()).getFriends().size(), 1);
        assertEquals(service.findById(user2.getId()).getFriends().size(), 1);
    }

    @Test
    public void addFriendShouldThrowNotFoundException() {
        service.create(user1);
        assertThrows(NotFoundException.class, () -> service.addFriend(user1.getId(), user2.getId()));
    }

    @Test
    public void removeFriendShouldClearFriendsList() {
        service.create(user1);
        service.create(user2);
        service.addFriend(user1.getId(), user2.getId());
        assertEquals(service.findById(user1.getId()).getFriends().size(), 1);
        service.removeFriend(user1.getId(), user2.getId());
        assertEquals(service.findById(user1.getId()).getFriends().size(), 0);

    }

    @Test
    public void removeFriendShouldThrowNotFoundException() {
        service.create(user1);
        assertThrows(NotFoundException.class, () -> service.removeFriend(user1.getId(), 99L));
    }

    @Test
    public void findFriendsOfUserShouldReturnFriendsList() {
        service.create(user1);
        service.create(user2);
        List<User> testList = new ArrayList<>();
        testList.add(user2);
        service.addFriend(user1.getId(), user2.getId());
        assertEquals(testList, service.findFriendsOfUser(user1.getId()));
    }

    @Test
    public void findCommonFriendsShouldReturnCommonFriends() {
        User user3 = new User(3, "test3@mail.ru", "Логин3", "Имя3",
                LocalDate.of(1895, 12, 28), new HashSet<>());
        List<User> testCommonFriends = new ArrayList<>();
        testCommonFriends.add(user3);
        service.create(user1);
        service.create(user2);
        service.create(user3);
        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user1.getId(), user3.getId());
        service.addFriend(user2.getId(), user3.getId());
        assertEquals(service.findCommonFriends(user1.getId(), user2.getId()), testCommonFriends);
    }

}
