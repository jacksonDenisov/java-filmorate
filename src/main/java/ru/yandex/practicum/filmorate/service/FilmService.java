package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmDbStorage storage) {
        this.storage = storage;
    }

    public List<Film> findAll() {
        log.info("Возвращаем список всех фильмов");
        return storage.findAll();
    }

    public Film findById(Long id) {
        log.info("Возвращаем фильм с id " + id);
        return storage.findById(id);
    }

    public Film create(Film film) {
        log.info("Создаем новый фильм " + film.toString());
        if (film.getDescription().length() >= 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Фильм не прошел валидацию!");
        }
        return storage.create(film);
    }

    public Film update(Film film) {
        log.info("Обновляем фильм " + film.toString());
        if (film.getDescription().length() >= 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Фильм не прошел валидацию!");
        }
        return storage.update(film);
    }

    public void likeFilm(long id, long userId) {
        storage.likeFilm(id, userId);
        log.info("Поставили лайк фильму " + id + " от пользователя " + userId);
    }

    public void removeLike(long id, long userId) {
        storage.removeLike(id, userId);
        log.info("Убрали лайк фильма " + id + " от пользователя " + userId);
    }

    public List<Film> findMostPopularFilms(long count) {
        log.info("Возвращаем список популярных фильмов.");
        return storage.findMostPopularFilms(count);
    }
}
