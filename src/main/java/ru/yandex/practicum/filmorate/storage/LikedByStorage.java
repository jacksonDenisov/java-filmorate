package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface LikedByStorage {

    boolean isLikeExist(long id, long userId);

    Set<Long> getLikedByOfFilm(long id);

    List<Film> findMostPopularFilms(long count);

    void likeFilm(long id, long userId);

    void removeLike(long id, long userId);
}
