package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User create(User user) throws ValidationException;

    public User update(User user) throws ValidationException;

    public User delete(User id, User userId);

    public List<User> findAll();
}
