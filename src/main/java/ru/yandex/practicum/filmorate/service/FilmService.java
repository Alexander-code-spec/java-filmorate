package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Service
public class FilmService {
    private InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage){
        this.filmStorage = filmStorage;

    }

    public Film putLike(Integer filmId, Integer userId) {
        Film film = (Film) filmStorage.getMap().get(filmId);
        film.getLikes().add(Long.valueOf(userId));
        return film;
    }

    public Film removeLike(Film film, Integer userId){
        if(userId < 0){
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        } else if (!film.getLikes().contains(Long.valueOf(userId))) {
            throw new ObjectNotFoundException(String.format("Пользователь  с id = \"%s\" не найден!", userId));
        }
        film.getLikes().remove(Long.valueOf(userId));
        return film;
    }

    public List<Film> getTopMovies(Integer count){
        SortedSet<Film> topFilms = new TreeSet<>((o1, o2) -> {
            int size1 = o1.getLikes().size();
            int size2 = o2.getLikes().size();
            if (size1 == 0) {
                return 1;
            }
            if (size2 == 0) {
                return -1;
            }
            return Integer.compare(size2, size1);
        });
        HashMap<Integer, Film> films = filmStorage.getMap();

        topFilms.addAll(films.values());
        List<Film> filmsList = new ArrayList<>(topFilms);

        return filmsList.subList(0, count>filmsList.size()?filmsList.size():count);
    }

    public InMemoryFilmStorage getFilmStorage() {
        return filmStorage;
    }
}
