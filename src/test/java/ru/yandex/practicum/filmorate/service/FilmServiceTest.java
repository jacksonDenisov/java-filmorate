package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage.InMemoryFilmStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmServiceTest {

    private FilmService service;
    private Film film1;
    private Film film2;


    @BeforeEach
    public void setup() {
        this.service = new FilmService(new InMemoryFilmStorage());
        this.film1 = new Film(1, "Фильм", "Описание",
                LocalDate.of(1895, 12, 28), 50, new MPA(), new ArrayList<>(), new HashSet<>());
        this.film2 = new Film(2, "Фильм2", "Описание2",
                LocalDate.of(1897, 12, 28), 70, new MPA(), new ArrayList<>(), new HashSet<>());
    }

    @Test
    public void likeFilmShouldFillLikedBy() {
        Set<Long> testLikedBy = new HashSet<>();
        service.create(film1);
        assertEquals(film1.getLikedBy(), testLikedBy);
        service.likeFilm(film1.getId(), 1);
        testLikedBy.add(1L);
        assertEquals(film1.getLikedBy(), testLikedBy);
    }

    @Test
    public void removeLikeShouldRemoveLikeFromLikedBy() {
        Set<Long> testLikedBy = new HashSet<>();
        service.create(film1);
        assertEquals(film1.getLikedBy(), testLikedBy);
        service.likeFilm(film1.getId(), 1L);
        service.removeLike(film1.getId(), 1L);
        assertEquals(film1.getLikedBy().size(), 0);
    }


    @Test
    public void removeLikeShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> service.likeFilm(33L, 33L));
    }

    @Test
    public void findMostPopularFilmsShouldReturnPopularList() {
        Set<Long> testLikedBy = new HashSet<>();
        Set<Long> testLikedBy2 = new HashSet<>();
        service.create(film1);
        service.create(film2);
        testLikedBy.add(1L);
        service.likeFilm(film1.getId(), 1);
        testLikedBy2.add(2L);
        service.likeFilm(film2.getId(), 1);
        testLikedBy2.add(3L);
        service.likeFilm(film2.getId(), 2);
        film1.setLikedBy(testLikedBy);
        film2.setLikedBy(testLikedBy2);
        List<Film> testPopularFilms = new ArrayList<>();
        List<Film> testPopularFilms2 = new ArrayList<>();
        testPopularFilms.add(film2);
        testPopularFilms2.add(film2);
        testPopularFilms2.add(film1);
        assertEquals(service.findMostPopularFilms(1), testPopularFilms);
        assertEquals(service.findMostPopularFilms(5), testPopularFilms2);
    }

}
