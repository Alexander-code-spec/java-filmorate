package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.AbstractStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Slf4j
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage {
    private Integer usersId = 0;

    @Override
    public User create(User user) throws ValidationException {
        if(user.getLogin().contains(" ")) {
            log.debug("Возникла ошибка при валдиации объекта: {}", user);
            throw new ValidationException("Логин не должен содержать пробелы!");
        }
        if(user.getName() == null || user.getName().isEmpty()){
            user.setName(user.getLogin());
        }
        this.usersId += 1;
        user.setId(usersId);
        return create(user, user.getId());
    }

    @Override
    public User update(User user) throws ValidationException {
        return update(user, user.getId());
    }

    @Override
    public User delete(User user) {
        return getMap().remove(user.getId());
    }

    @Override
    public List<User> findAll() {
        List<User> users = find();
        log.debug("Текущее количество пользователей : {}", users.size());
        return users;
    }

    @Override
    public User get(int id) {
        return getMap().get(id);
    }
}
