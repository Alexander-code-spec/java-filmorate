package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage extends AbstractStorage<Film> implements FilmStorage {
    private Integer filmId = 0;

    @Override
    public Film create(Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        } else if (getMap().containsKey(film.getId())) {
            throw new ValidationException("Объект с id = " + film.getId() + " уже сущесвтует");
        }
        this.filmId +=1;
        film.setId(filmId);
        return create(film, film.getId());
    }

    @Override
    public Film update(Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }
        return update(film, film.getId());
    }

    @Override
    public Film delete(Film film) {
        return getMap().remove(film.getId());
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = find();
        log.debug("Текущее количество фильмов : {}", films.size());
        return films;
    }

    @Override
    public Film get(int id) {
        return getMap().get(id);
    }

    @Override
    public List<Film> getLikesCount() {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsByYear(Integer id) {
        return null;
    }

    @Override
    public List<Film> getDirectorFilmsByRating(Integer id) {
        return null;
    }
}
