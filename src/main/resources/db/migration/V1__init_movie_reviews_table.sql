CREATE TABLE movie_reviews (
    imdb_id    VARCHAR(255)  NOT NULL,
    user_name  VARCHAR(255)  NOT NULL,
    user_score NUMERIC(2, 1) NOT NULL,
    PRIMARY KEY (imdb_id, user_name)
);
