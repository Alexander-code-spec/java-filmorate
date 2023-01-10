package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

public interface Storage<T> {
    T create(T obj) throws ValidationException;

    T update(T obj) throws ValidationException;

    Boolean delete(T obj1);

    List<T> findAll();
}
