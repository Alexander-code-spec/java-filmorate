package ru.yandex.practicum.filmorate.controller;

import org.hibernate.validator.internal.metadata.aggregated.rule.OverridingMethodMustNotAlterParameterConstraints;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film>{
    private HashMap<Integer, Film> films = new HashMap<>();
    private Integer filmId = 0;

    @GetMapping
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody @Valid  Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }
        valid(film, film.getId(), films, true);
        this.filmId +=1;
        film.setId(filmId);
        return abstractCreate(film, films, film.getId());
    }

    @PutMapping
    @Validated
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }
        valid(film, film.getId(), films, false);
        return abstractCreate(film, films, film.getId());
    }

    public HashMap<Integer, Film> getFilms() {
        return films;
    }
}
