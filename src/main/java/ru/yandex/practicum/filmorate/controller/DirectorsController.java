package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorsController {
    private final DirectorDao directorDao;

    @GetMapping
    public Collection<Director> getAllDirectors(){
        return directorDao.getDirectorsList();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Integer id){
        return directorDao.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director){
        return directorDao.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director){
        return directorDao.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") Integer id){
        directorDao.deleteDirector(id);
    }
}
