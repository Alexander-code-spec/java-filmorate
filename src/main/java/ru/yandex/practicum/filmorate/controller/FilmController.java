package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


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
    public void putLike(@PathVariable("id") Integer id, @PathVariable() Integer userId) {
        filmService.putLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count < 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getTopMovies(count);
    }

    @GetMapping("/common")
    @ResponseBody
    public List<Film> getCommonMovies(@RequestParam(defaultValue = "-1") Integer userId, @RequestParam(defaultValue = "-1") Integer friendId) {
        return filmService.getCommonMovies(userId, friendId);
    }

    @PostMapping
    public Film create(@RequestBody @Valid  Film film) throws ValidationException {
        return filmService.getFilmStorage().create(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        return filmService.removeLike(filmService.getFilmStorage().get(id), userId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable("directorId") Integer directorId, @RequestParam String sortBy) {
        return filmService.getSortDirector(directorId, sortBy);
    }

    @PutMapping
    public Film update(@RequestBody @Valid  Film film) throws ValidationException {
        return filmService.getFilmStorage().update(film);
    }

    @DeleteMapping
    public Boolean deleteFilm(@Valid @RequestBody Film film){
        if(filmService.getFilmStorage().delete(film) == null){
            throw new ObjectNotFoundException("Фильм не существует!");
        }
        return true;
    }

    @DeleteMapping("/{filmId}")
    public Boolean deleteFilmById(@PathVariable("filmId") Integer filmId){
        if(filmService.getFilmStorage().get(filmId) == null){
            throw new ObjectNotFoundException("Фильм не существует!");
        }
        else {
            filmService.getFilmStorage().delete(filmService.getFilmStorage().get(filmId));
        }
        return true;
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Film> search(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.search(query, by.contains("title"), by.contains("director"));
    }
}
