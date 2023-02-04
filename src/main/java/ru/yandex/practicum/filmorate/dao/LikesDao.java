package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface LikesDao {
    void createFilmLike(Integer filmId, Integer userId);

    void deleteFilmLike(Integer filmId, Integer userId);

    List<Integer> getAllFilmLikes(Integer filmId);
}
