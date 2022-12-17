package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    private Integer usersId = 0;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @Validated
    public User create(@RequestBody @Valid  User user) throws ValidationException {
        if(users.containsKey(user.getId()) || user.getLogin().contains(" ")) {
            log.debug("Возникла ошибка при валдиации объекта: {}", user);
            throw new ValidationException("Ошибка валидации");
        }
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        this.usersId += 1;
        user.setId(usersId);
        users.put(user.getId(), user);
        log.debug("Добавлен новый Пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        if(!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не существует");
        }

        users.put(user.getId(), user);
        log.debug("Изменен пользователь с id: {}", user);
        return user;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
