package ru.yandex.practicum.filmorate.storage;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryFilmStorageTest {
    private FilmStorage storage;
    private static Film film1;
    private static Film film2;

    @BeforeAll
    public static void createTestObjects() {
        film1 = new Film(1, "Фильм", "Описание",
                LocalDate.of(1895, 12, 28), 50, new MPA(1, "test"), new ArrayList<>(), new HashSet<>());
        film2 = new Film(2, "Фильм2", "Описание2",
                LocalDate.of(1897, 12, 28), 70, new MPA(1, "test"), new ArrayList<>(), new HashSet<>());
    }

    @BeforeEach
    public void setup() {
        this.storage = new InMemoryFilmStorage();
    }

    @Test
    public void findAllShouldReturnCorrectFilmsList() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(film1);
        assertEquals(storage.findAll().size(), 1);
    }

    @Test
    public void findByIdShouldReturnFilmById() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(film1);
        assertEquals(film1, storage.findById(film1.getId()));
    }

    @Test
    public void findByIdShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> storage.findById(33L));
    }

    @Test
    public void createShouldCreateFilm() {
        assertEquals(storage.findAll().size(), 0);
        storage.create(film1);
        assertEquals(storage.findAll().size(), 1);
    }


    @Test
    public void updateShouldUpdateFilm() {
        storage.create(film1);
        film1.setName("Новое имя");
        storage.update(film1);
        assertEquals(storage.findById(film1.getId()).getName(), "Новое имя");
    }

    @Test
    public void updateShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> storage.update(film1));
    }

}
