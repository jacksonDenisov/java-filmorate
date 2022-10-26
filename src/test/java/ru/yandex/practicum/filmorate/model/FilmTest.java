package ru.yandex.practicum.filmorate.model;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class FilmTest {
    private Film film;

    @Test
    public void createFilmTest() {
        film = new Film(1, "Фильм", "Описание", LocalDate.of(1895, 12, 28), 50);
        assertEquals(1, film.getId());
        assertEquals("Фильм", film.getName());
        assertEquals(50, film.getDuration());
        assertEquals("Описание", film.getDescription());
        assertEquals(LocalDate.of(1895, 12, 28), film.getReleaseDate());
    }


}
