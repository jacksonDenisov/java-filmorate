--DELETE
--FROM users;

--DELETE
--FROM friends;

ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;

MERGE INTO mpa KEY (id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genres KEY (id)
VALUES ( 1, 'COMEDY' ),
       ( 2, 'DRAMA' ),
       ( 3, 'CARTOON' ),
       ( 4, 'THRILLER' ),
       ( 5, 'DOCUMENTARY' ),
       ( 6, 'ACTION' )
;
