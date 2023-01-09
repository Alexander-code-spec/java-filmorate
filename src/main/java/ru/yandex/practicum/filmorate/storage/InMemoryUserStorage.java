package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;


@Component
@Slf4j
public class InMemoryUserStorage extends AbstractStorage implements UserStorage{
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
        return (User) abstractCreate(user, user.getId());
    }

    @Override
    public User update(User user) throws ValidationException {
        return (User) abstractUpdate(user, user.getId());
    }

    @Override
    public User delete(User user, User friend) {
        if(!user.getFriends().contains((long) friend.getId())) {
            throw new ObjectNotFoundException("В дурзьях нет пользователя с id = " + friend.getId());
        }
        user.getFriends().remove((long) friend.getId());
        friend.getFriends().remove((long) user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        return abstractFindAll("пользователей");
    }
}
