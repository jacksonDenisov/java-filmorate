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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.LikedByStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final MpaStorage mpaStorage;
    private final GenresStorage genresStorage;
    private final LikedByStorage likedByStorage;
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaStorage, GenresDbStorage genresStorage, LikedByStorage likedByStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genresStorage = genresStorage;
        this.likedByStorage = likedByStorage;
    }

    @Override
    public Film findById(Long id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Фильм с id " + id + " не найден.");
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet("SELECT id FROM films ORDER BY id ASC");
        while (filmIdRows.next()) {
            films.add(findById(filmIdRows.getLong("id")));
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlQueryFilm = "INSERT INTO films (name, description, release_date, duration, id_mpa) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(sqlQueryFilm, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, film.getDuration());
                    ps.setLong(5, film.getMpa().getId());
                    return ps;
                }, keyHolder
        );
        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (film.getGenres() != null) {
            String sqlQueryGenre = "INSERT INTO film_genre VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQueryGenre, filmId, genre.getId());
            }
        } else {
            log.info("Список переданных жанров для фильма пуст.");
        }
        return findById(filmId);
    }

    @Override
    public Film update(Film film) {
        String sqlQueryFilm = "UPDATE films SET name = ?, description = ?, release_date = ? , duration = ?, id_mpa = ? WHERE ID = ?";
        jdbcTemplate.update(
                sqlQueryFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            String sqlQueryGenre = "DELETE FROM film_genre WHERE film_id = ?";
            jdbcTemplate.update(sqlQueryGenre, film.getId());

            for (Genre genre : film.getGenres()) {
                sqlQueryGenre = "MERGE INTO film_genre KEY(film_id, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sqlQueryGenre, film.getId(), genre.getId());
            }
        } else {
            log.info("Список переданных жанров для фильма пуст.");
        }
        return findById(film.getId());
    }

    @Override
    public void likeFilm(long id, long userId) {
        likedByStorage.likeFilm(id, userId);
    }

    @Override
    public void removeLike(long id, long userId) {
        likedByStorage.removeLike(id, userId);
    }


    protected Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaStorage.findById(rs.getLong("id_mpa")))
                .genres(genresStorage.getFilmGenres(id))
                .likedBy(likedByStorage.getLikedByOfFilm(id))
                .build();
    }

}
