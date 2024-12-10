CREATE TABLE movie_schedule (
    booking   UUID          UNIQUE,
    imdb_id   CHAR(9)       NOT NULL,
    price     NUMERIC(4, 2) NOT NULL,
    show_time TIMESTAMP     PRIMARY KEY
);

CREATE INDEX idx_show_time ON movie_schedule (show_time);
