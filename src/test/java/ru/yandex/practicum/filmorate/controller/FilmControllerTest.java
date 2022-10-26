package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;


public class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    public void setup() {
        this.controller = new FilmController();
    }


    @Test
    public void createShouldReturnOkWhenFilmIsCorrect() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50);
        assertEquals(ResponseEntity.ok().body(film), controller.create(film));
    }
    @Test
    public void createShouldReturnBadRequestWhenFailFilmDescription() {
        Film film = new Film(1, "Фильм", "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который " +
                "задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                "стал кандидатом Коломбани.",
                LocalDate.of(1895, 12, 28), 50);
        assertEquals(ResponseEntity.badRequest().body(null), controller.create(film));
    }

    @Test
    public void createShouldReturnBadRequestWhenFailFilmReleaseDate() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1890, 03, 25), 50);
        assertEquals(ResponseEntity.badRequest().body(null), controller.create(film));
    }

    @Test
    public void createShouldReturnInternalServerErrorWhenFilmIsAlreadyExist() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50);
        controller.create(film);
        assertEquals(ResponseEntity.internalServerError().body(null), controller.create(film));
    }

    @Test
    public void findAllShouldReturnArrayOfFilms(){
        controller.create(new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50));
        ArrayList<Film> allFilms = controller.findAll();
        assertEquals(1, allFilms.size());
    }

    @Test
    public void updateShouldReturnOkWhenFilmIsCorrect() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50);
        controller.create(film);
        film.setName("Новый фильм");
        film.setDescription("Новое описание");
        assertEquals(ResponseEntity.ok().body(film), controller.update(film));
    }

    @Test
    public void updateShouldReturnBadRequestWhenFailFilmDescription() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50);
        controller.create(film);
        film.setName("Новый фильм");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который " +
                        "задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                        "стал кандидатом Коломбани.");
        assertEquals(ResponseEntity.badRequest().body(null), controller.update(film));
    }

    @Test
    public void updateShouldReturnBadRequestWhenFailFilmReleaseDate() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50);
        controller.create(film);
        film.setName("Новый фильм");
        film.setReleaseDate(LocalDate.of(1890, 12, 28));
        assertEquals(ResponseEntity.badRequest().body(null), controller.update(film));
    }

    @Test
    public void updateShouldReturnInternalServerErrorWhenFilmIsNotExist() {
        Film film = new Film(1, "Фильм", "description",
                LocalDate.of(1895, 12, 28), 50);
        assertEquals(ResponseEntity.internalServerError().body(null), controller.update(film));
    }


}
