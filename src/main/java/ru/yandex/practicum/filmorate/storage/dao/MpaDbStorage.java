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
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public MPA findById(long id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";
        MPA mpa;
        try {

            mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("MPA с id " + id + " не найдено.");
            throw new NotFoundException("MPA не найдено");
        }
        return mpa;
    }

    @Override
    public List<MPA> findAll(){
        List<MPA> allMpa = new ArrayList<>();
        SqlRowSet mpaIdRows = jdbcTemplate.queryForRowSet("SELECT id FROM mpa");
        while (mpaIdRows.next()) {
            allMpa.add(findById(mpaIdRows.getLong("id")));
        }
        return allMpa;
    }


    private MPA mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

}
