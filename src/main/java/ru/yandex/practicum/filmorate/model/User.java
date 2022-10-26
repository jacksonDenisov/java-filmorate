package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email (message = "E-mail пользователя не нашел валидацию.")
    @NotBlank (message = "E-mail пользователя не нашел валидацию - пустое значение.")
    private String email;
    @NotBlank (message = "Логин пользователя не прошел валидацю.")
    private String login;
    private String name;
    @Past (message = "Дата рождения не прошла валидацию.")
    private LocalDate birthday;

    public User(){
    }

    public User(int id, String email, String login, String name, LocalDate birthday){
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
