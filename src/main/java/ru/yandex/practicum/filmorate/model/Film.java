package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<Genre> genre;
    private MPA mpa;
    private Set<Long> likedBy = new HashSet<>();
}
