package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Map;

public interface LikesDao {
    void createFilmLike(Integer filmId, Integer userId);

    void deleteFilmLike(Integer filmId, Integer userId);

    List<Integer> getAllFilmLikes(Integer filmId);

    Map <Integer, Map<Integer, Integer>> findLikesForAllUsers();

}
