package ru.yandex.practicum.filmorate.exeptions;

public class UserValidationExeption extends RuntimeException {


    public UserValidationExeption(final String message) {
        super(message);
    }

    public UserValidationExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserValidationExeption(final Throwable cause) {
        super(cause);
    }
}
