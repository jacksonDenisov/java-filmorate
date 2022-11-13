DELETE
FROM users;

DELETE
FROM friends;

DELETE
FROM films;

DELETE
FROM LIKED_BY;

DELETE
FROM FILM_GENRE;

ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;

ALTER TABLE films
    ALTER COLUMN id RESTART WITH 1;

MERGE INTO mpa KEY (id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genres KEY (id)
VALUES ( 1, 'Комедия' ),
       ( 2, 'Драма' ),
       ( 3, 'Мультфильм' ),
       ( 4, 'Триллер' ),
       ( 5, 'Документальный' ),
       ( 6, 'Боевик' )
;
