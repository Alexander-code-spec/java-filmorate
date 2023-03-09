package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

public interface Storage<T> {
    T create(T obj) throws ValidationException;

    T update(T obj) throws ValidationException;

    T delete(T obj1);

    List<T> findAll();

    T get(int id);
}
