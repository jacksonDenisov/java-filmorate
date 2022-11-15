package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresDbStorage;

import java.util.List;

@Service
@Slf4j
public class GenresService {

    private final GenresDbStorage storage;

    @Autowired
    public GenresService(GenresDbStorage storage) {
        this.storage = storage;
    }


    public List<Genre> findAll() {
        log.info("Возвращаем список жанров");
        return storage.findAll();
    }

    public Genre findById(Long id) {
        log.info("Возвращаем жанр с id " + id);
        return storage.findById(id);
    }

}
