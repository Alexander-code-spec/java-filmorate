package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {
    Collection<Genre> getAllGenre();

    Collection<Genre> getAllFilmGenre(Integer id);
    Genre getGenreById(Integer id);
    void createFilmGenre(Integer filmId, Genre genre);

    void deleteFilmGenre(Integer id);
}
