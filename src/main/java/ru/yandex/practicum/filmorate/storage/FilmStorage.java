package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage extends Storage<Film> {
    HashMap<Integer, Film> getMap();
}
