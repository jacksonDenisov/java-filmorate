package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Film findById(Long id);

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void likeFilm(long id, long userId);

    void removeLike(long id, long userId);


}
