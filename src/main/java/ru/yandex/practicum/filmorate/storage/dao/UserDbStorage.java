package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;
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
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        user.setFriends(findListFriendsOfUsers(id));
        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        HashMap<Long, Set<Long>> friendsOfAllUsers = findFriendsOfAllUsers();
        for (User user : users) {
            if (friendsOfAllUsers.containsKey(user.getId())){
                user.setFriends(friendsOfAllUsers.get(user.getId()));
            }
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

    public Set<Long> findListFriendsOfUsers(Long id) {
        Set<Long> friends = new HashSet<>();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends WHERE user_id = ?", id);
        while (friendsRows.next()) {
            friends.add(friendsRows.getLong("friend_id"));
        }
        return friends;
    }

    public HashMap<Long, Set<Long>> findFriendsOfAllUsers() {
        HashMap<Long, Set<Long>> allUsersFriends = new HashMap<>();
        Set<Long> friends;
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends");
        while (friendsRows.next()) {
            long userId = friendsRows.getLong("user_id");
            long friendId = friendsRows.getLong("friend_id");
            if (!allUsersFriends.containsKey(userId)) {
                friends = new HashSet<>();
                friends.add(friendId);
                allUsersFriends.put(userId, friends);
            }
            allUsersFriends.get(userId).add(friendId);
        }
        return allUsersFriends;
    }

    protected User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getDate("birthday").toLocalDate(),
                new HashSet<>());
    }

}