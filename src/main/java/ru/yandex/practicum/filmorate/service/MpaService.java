package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final MpaStorage storage;

    @Autowired
    public MpaService(MpaDbStorage storage) {
        this.storage = storage;
    }


    public List<MPA> findAll() {
        log.info("Возвращаем список рейтингов");
        return storage.findAll();
    }

    public MPA findById(Long id) {
        log.info("Возвращаем рейтинг с id " + id);
        return storage.findById(id);
    }

}
