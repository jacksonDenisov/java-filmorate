package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    public Film findById(Long id){
        return new Film();
    }

    public List<Film> findAll(){
        return new ArrayList<>();
    }

    public Film create(Film film){
        return new Film();
    }

    public Film update(Film film){
        return new Film();
    }
}
