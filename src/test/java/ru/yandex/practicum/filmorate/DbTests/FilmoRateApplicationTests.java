package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenresStorage genresStorage;
    private final MpaStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;


    User user1;
    User user2;
    Film film1;
    Film film2;
    MPA mpa = new MPA(1, "test");
    Genre genre = new Genre(1, "Комедия");
    List<Genre> listGenres = new ArrayList<>();


    @BeforeEach
    public void setup() {
        user1 = new User(1, "Имя", "test@mail.ru", "Логин",
                LocalDate.of(1895, 12, 28), new HashSet<>());
        user2 = new User(2, "Имя2", "test2@mail.ru", "Логин2",
                LocalDate.of(1894, 12, 28), new HashSet<>());
        film1 = new Film(1, "Фильм", "Описание",
                LocalDate.of(1895, 12, 28), 50, mpa, new ArrayList<>(), new HashSet<>());
        film2 = new Film(2, "Фильм2", "Описание2",
                LocalDate.of(1898, 12, 28), 40, mpa, new ArrayList<>(), new HashSet<>());
        listGenres.add(genre);
    }

    @AfterEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM liked_by");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }


    @Test
    public void createAndFindUserTest() {
        userStorage.create(user1);
        assertEquals(userStorage.findById(1).getName(), "Имя");
    }

    @Test
    public void FindAllUsersTest() {
        userStorage.create(user1);
        userStorage.create(user2);
        assertEquals(userStorage.findAll().size(), 2);
    }

    @Test
    public void updateUserTest() {
        userStorage.create(user1);
        user1.setName("newName");
        userStorage.update(user1);
        assertEquals(userStorage.findById(1).getName(), "newName");
    }

    @Test
    public void isIdExistUserTest() {
        userStorage.create(user1);
        assertTrue(userStorage.isIdExist(user1.getId()));
        assertFalse(userStorage.isIdExist(9999));
    }


    @Test
    public void addAndRemoveFriendTest() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.addFriend(user1.getId(), user2.getId());
        SqlRowSet idRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends WHERE user_id = ? AND friend_id = ?", user1.getId(), user2.getId());
        assertTrue(idRows.next());
        userStorage.removeFriend(user1.getId(), user2.getId());
        idRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends WHERE user_id = ? AND friend_id = ?", user1.getId(), user2.getId());
        assertFalse(idRows.next());
    }

    @Test
    public void findFriendsOfUserTest() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.addFriend(user1.getId(), user2.getId());
        assertEquals(userStorage.findFriendsOfUser(user1.getId()).get(0), userStorage.findById(user2.getId()));
    }

    @Test
    public void findCommonFriendsTest() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user2);
        userStorage.addFriend(2, 1);
        userStorage.addFriend(3, 1);
        assertEquals(userStorage.findCommonFriends(2, 3).get(0), userStorage.findById(1));
    }


    @Test
    public void createAndFindFilmTest() {
        filmDbStorage.create(film1);
        assertEquals(filmDbStorage.findById(film1.getId()).getName(), "Фильм");
    }

    @Test
    public void findAllFilmsTest() {
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        assertEquals(filmDbStorage.findAll().size(), 2);
        assertEquals(filmDbStorage.findAll().get(0), filmDbStorage.findById(film1.getId()));
    }

    @Test
    public void updateFindFilmTest() {
        filmDbStorage.create(film1);
        film1.setName("newNameForTest");
        filmDbStorage.update(film1);
        assertEquals(filmDbStorage.findById(user1.getId()).getName(), "newNameForTest");
    }

    @Test
    public void likeAndRemoveFilmTest() {
        filmDbStorage.create(film1);
        userStorage.create(user1);
        filmDbStorage.likeFilm(1, 1);
        SqlRowSet idRows = jdbcTemplate.queryForRowSet("SELECT * FROM liked_by WHERE film_id = ? AND user_id = ?", film1.getId(), user1.getId());
        assertTrue(idRows.next());
        assertEquals(idRows.getLong("film_id"), 1);
        assertEquals(idRows.getLong("user_id"), 1);
        filmDbStorage.removeLike(1, 1);
        idRows = jdbcTemplate.queryForRowSet("SELECT * FROM liked_by WHERE film_id = ? AND user_id = ?", film1.getId(), user1.getId());
        assertFalse(idRows.next());
    }

    @Test
    public void findByIdGenreTest() {
        film1.setGenres(listGenres);
        assertEquals(genresStorage.findById(1).getName(), "Комедия");
    }

    @Test
    public void findAllGenreTest(){
        assertEquals(genresStorage.findAll().size(), 6);
        assertEquals(genresStorage.findAll().get(0).getName(), "Комедия");
    }

    @Test
    public void getFilmGenresTest(){
        film1.setGenres(listGenres);
        filmDbStorage.create(film1);
        assertEquals(filmDbStorage.getFilmGenres(1).get(0).getName(), "Комедия");
    }

    @Test
    public void findByIdMpaTest() {
        assertEquals(mpaStorage.findById(2).getName(), "PG");
    }

    @Test
    public void findAllMpaTest(){
        assertEquals(mpaStorage.findAll().size(), 5);
        assertEquals(mpaStorage.findAll().get(0).getName(), "G");
    }

    @Test
    public void likeFilmAndGetLikedByOfFilmTest() {
        userStorage.create(user1);
        filmDbStorage.create(film1);
        filmDbStorage.likeFilm(1,1);
        assertEquals(filmDbStorage.getLikedByOfFilm(1).size(), 1);
    }

    @Test
    public void isLikeExistLikedByTest() {
        userStorage.create(user1);
        filmDbStorage.create(film1);
        filmDbStorage.likeFilm(1,1);
        assertTrue(filmDbStorage.isLikeExist(1, 1));
    }

    @Test
    public void removeLikeLikedByTest(){
        userStorage.create(user1);
        filmDbStorage.create(film1);
        filmDbStorage.likeFilm(1,1);
        assertEquals(filmDbStorage.getLikedByOfFilm(1).size(), 1);
        filmDbStorage.removeLike(1, 1);
        assertEquals(filmDbStorage.getLikedByOfFilm(1).size(), 0);

    }
}
