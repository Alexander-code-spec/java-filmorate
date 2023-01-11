package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage extends Storage<User> {
    HashMap<Integer, User> getMap();
}
