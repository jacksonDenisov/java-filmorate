package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping("/films")
    protected List<Film> findAll() {
        return service.findAll();
    }

    @GetMapping("/films/popular")
    protected List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10") long count) {
        return service.findMostPopularFilms(count);
    }

    @GetMapping("/films/{id}")
    protected Film findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/films")
    protected Film create(@Valid @RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping("/films")
    protected Film update(@Valid @RequestBody Film film) {
        return service.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    protected void likeFilm(@PathVariable long id, @PathVariable long userId) {
        service.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    protected void removeLike(@PathVariable long id, @PathVariable long userId) {
        service.removeLike(id, userId);
    }
}