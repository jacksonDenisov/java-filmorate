package ru.yandex.practicum.filmorate.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;

public class FilmTest {

    @Test
    public void createFilmTest() {
        Film film = new Film(1, "Фильм", "Описание",
                LocalDate.of(1895, 12, 28), 50, new HashSet<>());
        assertEquals(1, film.getId());
        assertEquals("Фильм", film.getName());
        assertEquals(50, film.getDuration());
        assertEquals("Описание", film.getDescription());
        assertEquals(LocalDate.of(1895, 12, 28), film.getReleaseDate());
        assertEquals(film.getLikedBy().size(), 0);
    }

}
