package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    @Email(message = "E-mail пользователя не нашел валидацию.")
    @NotBlank(message = "E-mail пользователя не нашел валидацию - пустое значение.")
    private String email;
    @NotBlank(message = "Логин пользователя не прошел валидацю.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не прошла валидацию.")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
