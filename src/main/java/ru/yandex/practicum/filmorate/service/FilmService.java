package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesDao likeDao;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage")FilmStorage filmStorage,
                       @Qualifier("LikesDao")LikesDao likeDao,
                       @Qualifier("UserDbStorage")UserStorage userStorage){
        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.userStorage = userStorage;
    }

    public void putLike(Integer filmId, Integer userId) {
        likeDao.createFilmLike(filmId, userId);
    }

    public Optional<Film> removeLike(Film film, Integer userId){
        if(userId < 0){
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        } else if (!likeDao.getAllFilmLikes(film.getId()).contains(userId)) {
            throw new ObjectNotFoundException(String.format("Пользователь  с id = \"%s\" не найден!", userId));
        }

        likeDao.deleteFilmLike(film.getId(), userId);
        return Optional.of(film);
    }

    public List<Film> getTopMovies(Integer count){
        List<Film> films = filmStorage.getLikesCount();

        return films.subList(0, count>films.size()?films.size():count);
    }

    public List<Film> getCommonMovies(Integer userId, Integer friendId){
        userStorage.get(userId);
        userStorage.get(friendId);
        return filmStorage.getCommonMovies(userId, friendId);
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }
}
