package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
        SortedSet<Film> topFilms = new TreeSet<>((o1, o2) -> {
            int size1 = likeDao.getAllFilmLikes(o1.getId()).size();
            int size2 = likeDao.getAllFilmLikes(o2.getId()).size();
            if (size1 == 0) {
                return 1;
            }
            if (size2 == 0) {
                return -1;
            }
            return Integer.compare(size2, size1);
        });

        topFilms.addAll(filmStorage.findAll());
        List<Film> filmsList = new ArrayList<>(topFilms);

        return filmsList.subList(0, count>filmsList.size()?filmsList.size():count);
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }
}
