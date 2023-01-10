package ru.yandex.practicum.filmorate.storage;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashMap;
import java.util.List;


@Component
@Slf4j
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage{
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
        return Create(user, user.getId());
    }

    @Override
    public User update(User user) throws ValidationException {
        return update(user, user.getId());
    }

    @Override
    public Boolean delete(User user) {
        getMap().remove(user.getId());
        return true;
    }

    @Override
    public List<User> findAll() {
        return find("пользователей");
    }
}
