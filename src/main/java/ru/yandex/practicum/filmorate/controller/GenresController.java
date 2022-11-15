package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@RestController
@Slf4j
public class GenresController {

    private final GenresService service;

    @Autowired
    public GenresController(GenresService service) {
        this.service = service;
    }


    @GetMapping("/genres")
    protected List<Genre> findAll() {
        return service.findAll();
    }


    @GetMapping("/genres/{id}")
    protected Genre findById(@PathVariable Long id) {
        return service.findById(id);
    }

}
