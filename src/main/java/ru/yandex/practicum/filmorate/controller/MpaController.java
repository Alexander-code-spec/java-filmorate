package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaDao mpaDao;

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Optional<Mpa> getMpa(@PathVariable("id") Integer id) {
        return mpaDao.getMpaById(id);
    }
}
