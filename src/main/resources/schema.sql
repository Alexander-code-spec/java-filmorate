DROP TABLE IF EXISTS  Review_usefuls, Mpa, Genres, directors, Likes, Users, Films, Friendship, Reviews, Film_genre, films_directors;

CREATE TABLE IF NOT EXISTS  Users(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    birthdate DATE
);

CREATE TABLE IF NOT EXISTS Films(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    genre VARCHAR,
    mpa INTEGER,
    likes INTEGER
);

CREATE TABLE IF NOT EXISTS  Reviews(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR NOT NULL,
    isPositive BOOLEAN,
    user_Id INTEGER REFERENCES USERS(id) ON DELETE CASCADE NOT NULL,
    film_Id INTEGER REFERENCES FILMS(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS  Review_usefuls(
    review_id INTEGER REFERENCES Reviews(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES Users(id) ON DELETE CASCADE,
    useful INTEGER,
    CONSTRAINT review_ident PRIMARY KEY (review_id, user_id)
};

CREATE TABLE IF NOT EXISTS Directors(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendship (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    friend_id INTEGER  REFERENCES users(id) ON DELETE CASCADE,
    communication_status BIT DEFAULT(FALSE),
    CONSTRAINT friendship_ident PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS Likes(
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    film_id INTEGER REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT id PRIMARY KEY(user_id, film_id)
);

CREATE TABLE IF NOT EXISTS Genres (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS Mpa (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS  Film_genre(
    film_id INTEGER REFERENCES Films(id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES Genres(id) ON DELETE CASCADE,
    CONSTRAINT ident PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS films_directors (
    film_id INTEGER NOT NULL REFERENCES Films(id),
    director_id INTEGER REFERENCES Directors(id),
    PRIMARY KEY (film_id, director_id)
);






