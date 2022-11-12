package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.util.List;
import java.util.stream.Collectors;

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
        return storage.create(film);
    }

    public Film update(Film film) {
        log.info("Обновляем фильм " + film.toString());
        return storage.update(film);
    }

    public void likeFilm(long id, long userId) {
        storage.findById(id).getLikedBy().add(userId);
        log.info("Поставили лайк фильму " + id + " от пользователя " + userId);
    }

    public void removeLike(long id, long userId) {
        if (!storage.findById(id).getLikedBy().contains(userId)) {
            log.info("Этот пользователь не ставил лайку данному фильму.");
            throw new NotFoundException("Этот пользователь не ставил лайку данному фильму.");
        } else {
            storage.findById(id).getLikedBy().remove(userId);
            log.info("Убрали лайк фильма " + id + " от пользователя " + userId);
        }
    }

    public List<Film> findMostPopularFilms(long count) {
        log.info("Возвращаем список популярных фильмов.");
        return storage.findAll()
                .stream()
                .sorted((o1, o2) -> o2.getLikedBy().size() - o1.getLikedBy().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
