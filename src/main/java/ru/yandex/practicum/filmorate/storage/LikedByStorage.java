package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface LikedByStorage {

    boolean isLikeExist(long id, long userId);

    Set<Long> getLikedByOfFilm(long id);

    void likeFilm(long id, long userId);

    void removeLike(long id, long userId);
}
