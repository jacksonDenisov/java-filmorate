package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static long id = 0;


    @GetMapping
    protected List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    protected ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        try {
            if (films.containsKey(film.getId())) {
                log.warn("Такой фильм уже существует!");
                return ResponseEntity.internalServerError().body(film);
            } else if (film.getDescription().length() >= 200 ||
                    film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new FilmValidationException("Фильм не прошел валидацию!");
            }
            film.setId(++id);
            films.put(id, film);
            log.info("Фильм {} успешно добавлен", film.getName());
            return ResponseEntity.ok().body(film);
        } catch (FilmValidationException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(film);
        }
    }

    @PutMapping
    protected ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        try {
            if (!films.containsKey(film.getId())) {
                log.warn("Такого фильма нет в списке.");
                return ResponseEntity.internalServerError().body(film);
            } else if (film.getDescription().length() >= 200 ||
                    film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new FilmValidationException("Фильм не прошел валидацию!");
            }
            films.put(film.getId(), film);
            log.info("Фильм {} успешно обновлен", film.getName());
            return ResponseEntity.ok().body(film);
        } catch (FilmValidationException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(film);
        }
    }
}