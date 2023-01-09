package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film create(Film film) throws ValidationException;

    public Film update(Film film) throws ValidationException;

    public void delete();

    public List<Film> findAll();
}
