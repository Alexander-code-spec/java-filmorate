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
public class UserController extends AbstractController<User> {
    private HashMap<Integer, User> users = new HashMap<>();
    private Integer usersId = 0;

    @GetMapping
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @Validated
    public User create(@RequestBody @Valid  User user) throws ValidationException {
        if(user.getLogin().contains(" ")) {
                log.debug("Возникла ошибка при валдиации объекта: {}", user);
                throw new ValidationException("Логин не должен содержать пробелы!");
        }
        if(user.getName() == null || user.getName().isEmpty()){
            user.setName(user.getLogin());
        }
        valid(user, user.getId(), users, true);
        this.usersId += 1;
        user.setId(usersId);
        return abstractCreate(user, users, user.getId());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        valid(user, user.getId(), users, false);
        return abstractCreate(user, users, user.getId());
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
