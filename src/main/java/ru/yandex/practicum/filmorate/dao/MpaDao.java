package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaDao {
    Collection<Mpa> getAllMpa();

    Optional<Mpa> getMpaById(Integer id);
    Optional<Mpa> createFilmMpa(Film film, Integer id);
}
