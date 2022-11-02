package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    protected List<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/users/{id}")
    protected User findById(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping("/users/{id}/friends")
    protected List<User> findFriendsOfUser(@PathVariable long id) {
        return service.findFriendsOfUser(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    protected List<User> findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return service.findCommonFriends(id, otherId);
    }

    @PostMapping("/users")
    protected User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    @PutMapping("/users")
    protected User update(@Valid @RequestBody User user) {
        return service.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    protected void addFriend(@PathVariable long id, @PathVariable long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    protected void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        service.removeFriend(id, friendId);
    }

}