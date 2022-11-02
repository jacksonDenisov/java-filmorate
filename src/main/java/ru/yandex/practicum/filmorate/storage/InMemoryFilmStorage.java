package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private static long id = 0;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film findById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.info("Фильм не найден!");
            throw new NotFoundException("Фильм не найден!");
        }
    }

    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            throw new FilmValidationException("Такой фильм уже существует!");
        } else if (film.getDescription().length() >= 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Фильм не прошел валидацию!");
        }
        film.setId(++id);
        films.put(id, film);
        log.info("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Такого фильма нет в списке.");
        } else if (film.getDescription().length() >= 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Фильм не прошел валидацию!");
        }
        films.put(film.getId(), film);
        log.info("Фильм {} успешно обновлен", film.getName());
        return film;
    }

}
