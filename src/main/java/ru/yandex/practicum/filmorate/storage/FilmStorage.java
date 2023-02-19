package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage extends Storage<Film> {

    List<Film> getLikesCount();

    List<Film> getFilmsByIds(List<Integer> filmsIds);
}
