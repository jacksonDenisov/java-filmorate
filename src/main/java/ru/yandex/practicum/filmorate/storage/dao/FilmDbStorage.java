package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film findById(Long id) {
        String sqlQuery = "SELECT * FROM films JOIN mpa on films.id_mpa=mpa.id WHERE films.id = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        film.setLikedBy(getLikedByOfFilm(id));
        film.setGenres(getFilmGenres(id));
        return film;
    }


    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films JOIN mpa on films.id_mpa=mpa.id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        Map<Long, Set<Long>> allLikes = findAllFilmsLikes();
        Map<Long, List<Genre>> allGenres = getAllFilmsGenres();
        for (Film film : films) {
            if (allLikes.containsKey(film.getId())) {
                film.setLikedBy(allLikes.get(film.getId()));
            }
            if (allGenres.containsKey(film.getId())) {
                film.setGenres(allGenres.get(film.getId()));
            }
        }
        return films;
    }


    @Override
    public Film create(Film film) {
        String sqlQueryFilm = "INSERT INTO films (name, description, release_date, duration, id_mpa) " +
                "VALUES(?, ?, ?, ?, ?)";
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
            String sqlQueryGenre = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            updateFilmGenres(sqlQueryGenre, film.getGenres(), filmId);
        } else {
            log.info("Список переданных жанров для фильма пуст.");
        }
        return findById(filmId);
    }


    @Override
    public Film update(Film film) {
        String sqlQueryFilm = "UPDATE films SET name = ?, description = ?, " +
                "release_date = ? , duration = ?, id_mpa = ? WHERE ID = ?";
        jdbcTemplate.update(
                sqlQueryFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        String sqlDeleteGenre = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenre, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sqlUpdateGenre = "MERGE INTO film_genre KEY(film_id, genre_id) VALUES (?, ?)";
            updateFilmGenres(sqlUpdateGenre, film.getGenres(), film.getId());
        } else {
            log.info("Список переданных жанров для фильма пуст.");
        }
        return findById(film.getId());
    }


    @Override
    public void likeFilm(long id, long userId) {
        if (!isLikeExist(id, userId)) {
            String sqlQuery = "INSERT INTO liked_by (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId);
        }
    }


    @Override
    public void removeLike(long id, long userId) {
        if (!isLikeExist(id, userId)) {
            log.info("Пользователь " + userId + " не ставил лайк фильму " + id);
            throw new NotFoundException("Пользователь " + userId + " не ставил лайк фильму " + id);
        }
        String sqlQuery = "DELETE FROM liked_by WHERE film_id=? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }


    public boolean isLikeExist(long id, long userId) {
        SqlRowSet idRows = jdbcTemplate.queryForRowSet("SELECT * FROM liked_by " +
                "WHERE film_id = ? AND user_id = ?", id, userId);
        return idRows.next();
    }


    public Set<Long> getLikedByOfFilm(long id) {
        Set<Long> likedBy = new HashSet<>();
        SqlRowSet likedByIdRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM liked_by WHERE FILM_ID = ?", id);
        while (likedByIdRows.next()) {
            likedBy.add(likedByIdRows.getLong("user_id"));
        }
        return likedBy;
    }


    public Map<Long, Set<Long>> findAllFilmsLikes() {
        Map<Long, Set<Long>> allLikes = new HashMap<>();
        Set<Long> likes;
        SqlRowSet likedByIdRows = jdbcTemplate.queryForRowSet("SELECT * FROM liked_by ");
        while (likedByIdRows.next()) {
            long filmId = likedByIdRows.getLong("film_id");
            if (!allLikes.containsKey(filmId)) {
                likes = new HashSet<>();
                likes.add(likedByIdRows.getLong("user_id"));
                allLikes.put(filmId, likes);
            }
            allLikes.get(filmId).add(likedByIdRows.getLong("user_id"));
        }
        return allLikes;
    }


    public void updateFilmGenres(String sql, List<Genre> filmGenres, long filmId) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = filmGenres.get(i);
                ps.setLong(1, filmId);
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return filmGenres.size();
            }
        });
    }

    public List<Genre> getFilmGenres(long filmId) {
        List<Genre> filmGenres = new ArrayList<>();
        SqlRowSet genreInfoRows = jdbcTemplate.queryForRowSet("SELECT genres.id, genres.name " +
                "FROM film_genre JOIN genres on film_genre.genre_id=genres.id WHERE film_id = ?", filmId);
        while (genreInfoRows.next()) {
            filmGenres.add(new Genre(genreInfoRows.getLong("id"),
                    genreInfoRows.getString("name")));
        }
        return filmGenres;
    }


    public Map<Long, List<Genre>> getAllFilmsGenres() {
        Map<Long, List<Genre>> allGenres = new HashMap<>();
        List<Genre> filmGenres;
        SqlRowSet genreInfoRows = jdbcTemplate.queryForRowSet("SELECT * FROM film_genre JOIN genres on film_genre.genre_id=genres.id");
        while (genreInfoRows.next()) {
            filmGenres = new ArrayList<>();
            long filmId = genreInfoRows.getLong("film_id");
            if (!allGenres.containsKey(filmId)) {
                filmGenres.add(new Genre(genreInfoRows.getLong("id"),
                        genreInfoRows.getString("name")));
                allGenres.put(filmId, filmGenres);
            } else {
                allGenres.get(filmId).add(new Genre(genreInfoRows.getLong("id"),
                        genreInfoRows.getString("name")));
            }
        }
        return allGenres;
    }


    protected Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        return new Film(id,
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new MPA(rs.getLong("id_mpa"), rs.getString("mpa.name")),
                new ArrayList<>(),
                new HashSet<>());
    }

}
