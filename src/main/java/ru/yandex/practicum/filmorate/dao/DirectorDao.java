package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface DirectorDao {
    Collection<Director> getDirectorsList();
    Director getDirectorById(Integer id);
    Director updateDirector(Director director);
    Director createDirector(Director director);
    void deleteDirector(Integer id);

    void createFilmDirector(Integer filmId, Director genre);

    void deleteFilmDirector(Integer id);

    Collection<Director> getAllFilmDirector(Integer filmId);
}
