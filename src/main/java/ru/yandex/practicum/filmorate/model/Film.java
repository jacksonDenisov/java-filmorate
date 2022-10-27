package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private long id;
    @NotBlank(message = "Название фильма не прошло валидацию.")
    private String name;
    @NotBlank(message = "Описание фильма не прошло валидацию.")
    private String description;
    @NotNull(message = "Дата фильма Null")
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма не прошла валидацию.")
    private int duration;
}
