package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank (message = "Название фильма не прошло валидацию.")
    private String name;
    @NotBlank (message = "Описание фильма не прошло валидацию.")
    private String description;
    @NotNull (message = "Дата фильма Null")
    private LocalDate releaseDate;
    @Positive (message = "Длительность фильма не прошла валидацию.")
    private int duration;


    public Film(){
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration){
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
