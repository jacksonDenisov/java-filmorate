package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GenresDbStorage implements GenresStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Genre findById(long id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Genre с id " + id + " не найден.");
            throw new NotFoundException("Genre не найден");
        }
        return genre;
    }

    @Override
    public List<Genre> findAll(){
        List<Genre> genres = new ArrayList<>();
        SqlRowSet genresIdRows = jdbcTemplate.queryForRowSet("SELECT id FROM genres");
        while (genresIdRows.next()) {
            genres.add(findById(genresIdRows.getLong("id")));
        }
        return genres;
    }

    @Override
    public List<Genre> getFilmGenres(long filmId){
        List<Genre> filmGenres = new ArrayList<>();
        SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet("SELECT genre_id FROM film_genre WHERE film_id = ?", filmId);
        while (filmIdRows.next()) {
            filmGenres.add(findById(filmIdRows.getLong("genre_id")));
        }
        return filmGenres;
    }


    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
