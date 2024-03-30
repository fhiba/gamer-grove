create sequence if not exists users_id_seq
    as integer;

CREATE TABLE IF NOT EXISTS users(
                     id INT PRIMARY KEY DEFAULT nextval('users_id_seq'),
                     username VARCHAR(50) NOT NULL,
                     password TEXT NOT NULL,
                     email VARCHAR(50) NOT NULL,
                     owner boolean NOT NULL DEFAULT FALSE
);

