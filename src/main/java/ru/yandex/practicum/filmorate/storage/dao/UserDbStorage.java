package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User findById(long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь с id " + id + " не найден.");
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        SqlRowSet userIdRows = jdbcTemplate.queryForRowSet("SELECT id FROM users");
        while (userIdRows.next()) {
            users.add(findById(userIdRows.getLong("id")));
        }
        return users;
    }

    @Override
    public User create(User user) {
        if (user.getLogin().contains(" ")) {
            throw new UserValidationException("Логин пользователя не прошел валидацию!");
        } else if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "INSERT INTO users (name, email, login, birthday) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getName());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getLogin());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                }, keyHolder
        );
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET name = ?, email = ?, login = ? , birthday = ? WHERE ID = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return findById(user.getId());
    }

    @Override
    public boolean isIdExist(long id) {
        SqlRowSet idRows = jdbcTemplate.queryForRowSet("SELECT id FROM users WHERE id = ?", id);
        return idRows.next();
    }

    @Override
    public void addFriend(long id, long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void removeFriend(long id, long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id=? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> findFriendsOfUser(long id) {
        List<User> userFriends = new ArrayList<>();
        SqlRowSet userFriendsIdsRows = jdbcTemplate
                .queryForRowSet("SELECT friend_id FROM friends WHERE user_id = ?", id);
        while (userFriendsIdsRows.next()) {
            userFriends.add(findById(userFriendsIdsRows.getLong("friend_id")));
        }
        return userFriends;
    }

    @Override
    public List<User> findCommonFriends(long id, long otherId) {
        return findFriendsOfUser(id)
                .stream()
                .filter(findFriendsOfUser(otherId)::contains)
                .collect(Collectors.toList());
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

}
