package ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private static long id = 0;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.info("Фильм не найден!");
            throw new NotFoundException("Фильм не найден!");
        }
    }

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            throw new FilmValidationException("Такой фильм уже существует!");
        }
        film.setId(++id);
        films.put(id, film);
        log.info("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Такого фильма нет в списке.");
        }
        films.put(film.getId(), film);
        log.info("Фильм {} успешно обновлен", film.getName());
        return film;
    }

    @Override
    public void likeFilm(long id, long userId) {
        findById(id).getLikedBy().add(userId);
    }

    @Override
    public void removeLike(long id, long userId) {
        if (!findById(id).getLikedBy().contains(userId)) {
            log.info("Этот пользователь не ставил лайку данному фильму.");
            throw new NotFoundException("Этот пользователь не ставил лайку данному фильму.");
        } else {
            findById(id).getLikedBy().remove(userId);
        }
    }

}