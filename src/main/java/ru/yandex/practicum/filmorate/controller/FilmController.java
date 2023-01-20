package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController{
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.getFilmStorage().findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Film getEmployeesById(@PathVariable("id") Integer id) {
        if(filmService.getFilmStorage().get(id)==null){
            throw new ObjectNotFoundException("Пользователь не существует!");
        }
        return filmService.getFilmStorage().get(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseBody
    public Film putLike(@PathVariable("id") Integer id, @PathVariable() Integer userId) {
        return filmService.putLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count < 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getTopMovies(count);
    }

    @PostMapping
    public Film create(@RequestBody @Valid  Film film) throws ValidationException {
        return filmService.getFilmStorage().create(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        return filmService.removeLike(filmService.getFilmStorage().get(id), userId);
    }

    @PutMapping
    @Validated
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.getFilmStorage().update(film);
    }

    @DeleteMapping
    public Boolean deleteFilm(@Valid @RequestBody Film film){
        if(filmService.getFilmStorage().delete(film) == null){
            throw new ObjectNotFoundException("Фильм не существует!");
        }
        return true;
    }
}
