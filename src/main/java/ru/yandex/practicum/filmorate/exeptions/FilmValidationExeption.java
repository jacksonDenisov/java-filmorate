package ru.yandex.practicum.filmorate.exeptions;

public class FilmValidationExeption extends RuntimeException {

    public FilmValidationExeption(final String message) {
        super(message);
    }

    public FilmValidationExeption(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FilmValidationExeption(final Throwable cause) {
        super(cause);
    }
}
