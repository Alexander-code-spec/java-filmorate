package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesDao likeDao;
    private final UserStorage userStorage;
    private final DirectorDao directorDao;
    private final FeedDao feedDao;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage")FilmStorage filmStorage,
                       @Qualifier("LikesDao")LikesDao likeDao,
                       @Qualifier("UserDbStorage")UserStorage userStorage,
                       FeedDao feedDao,
                       DirectorDao directorDao){
        this.filmStorage = filmStorage;
        this.likeDao = likeDao;
        this.userStorage = userStorage;
        this.feedDao = feedDao;
        this.directorDao = directorDao;
    }

    public void putLike(Integer filmId, Integer userId) {
        likeDao.createFilmLike(filmId, userId);
        feedDao.addToUserFeed(userId, filmId, FeedEventType.LIKE, FeedOperation.ADD);
    }

    public Optional<Film> removeLike(Film film, Integer userId){
        if(userId < 0){
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        } else if (!likeDao.getAllFilmLikes(film.getId()).contains(userId)) {
            throw new ObjectNotFoundException(String.format("Пользователь  с id = \"%s\" не найден!", userId));
        }

        likeDao.deleteFilmLike(film.getId(), userId);
        feedDao.addToUserFeed(userId, film.getId(), FeedEventType.LIKE, FeedOperation.REMOVE);
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

    public List<Film> getSortDirector(Integer id, String sort){
        if(directorDao.getDirectorById(id) == null){
            throw new ObjectNotFoundException("Такого режиссера не существует");
        }
        switch (sort){
            case "likes":
                return filmStorage.getDirectorFilmsByRating(id);
            case "year":
                return filmStorage.getDirectorFilmsByYear(id);
            default:
                throw new ObjectNotFoundException("Неверно задан параметр поиска");
        }
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public List<Film> search (String query, boolean isT, boolean isD) {
        return filmStorage.search(query, isT, isD);
    }
}
