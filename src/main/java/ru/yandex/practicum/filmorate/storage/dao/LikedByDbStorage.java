package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.LikedByStorage;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class LikedByDbStorage implements LikedByStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikedByDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isLikeExist(long id, long userId) {
        SqlRowSet idRows = jdbcTemplate.queryForRowSet("SELECT * FROM liked_by WHERE film_id = ? AND user_id = ?", id, userId);
        return idRows.next();
    }

    @Override
    public Set<Long> getLikedByOfFilm(long id) {
        Set<Long> likedBy = new HashSet<>();
        SqlRowSet likedByIdRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM liked_by WHERE FILM_ID = ?", id);
        while (likedByIdRows.next()) {
            likedBy.add(likedByIdRows.getLong("user_id"));
        }
        return likedBy;
    }

    @Override
    public void likeFilm(long id, long userId) {
        if (isLikeExist(id, userId)) {
            log.info("Лайк уже поставлен.");
        } else {
            String sqlQuery = "INSERT INTO liked_by (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId);
            log.info("Успешный лайк");
        }
    }

    @Override
    public void removeLike(long id, long userId) {
        if (!isLikeExist(id, userId)) {
            log.info("Этот пользователь не ставил лайку данному фильму.");
            throw new NotFoundException("Этот пользователь не ставил лайку данному фильму.");
        } else {
            String sqlQuery = "DELETE FROM liked_by WHERE film_id=? AND user_id = ?";
            jdbcTemplate.update(sqlQuery, id, userId);
        }
    }

}