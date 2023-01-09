package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage extends AbstractStorage implements FilmStorage {
    private Integer filmId = 0;

    @Override
    public Film create(Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }
        this.filmId +=1;
        film.setId(filmId);
        return (Film) abstractCreate(film, film.getId());
    }

    @Override
    public Film update(Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }
        return (Film) abstractUpdate(film, film.getId());
    }

    @Override
    public void delete() {
    }

    @Override
    public List<Film> findAll() {
        return abstractFindAll("фильмов");
    }
}
