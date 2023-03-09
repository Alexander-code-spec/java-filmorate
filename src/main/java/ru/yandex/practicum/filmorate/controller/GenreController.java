package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreDao genreDao;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenre();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Genre getGenreById(@PathVariable("id") Integer id) {
        return genreDao.getGenreById(id);
    }
}
