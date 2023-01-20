package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    public User addFriend(Integer userId, Integer friendId){
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if(user == null) {
            throw new ObjectNotFoundException("Пользователь не найден!");
        } else if(friend == null) {
            throw new ObjectNotFoundException(String.format("Друг с id = \"%s\" не найден!", friendId));
        }
        user.getFriends().add(Long.valueOf(friendId));
        friend.getFriends().add(Long.valueOf(userId));

        return user;
    }

    public User deleteFriend(Integer userId, Integer friendId){
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if(user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь  с id = \"%s\" не найден!", userId));
        } else if(friend == null) {
            throw new ObjectNotFoundException(String.format("Друг с id = \"%s\" не найден!", friendId));
        } else if(!user.getFriends().contains((long) friend.getId())) {
            throw new ObjectNotFoundException("В дурзьях нет пользователя с id = " + friend.getId());
        }
        user.getFriends().remove((long) friend.getId());
        friend.getFriends().remove((long) user.getId());

        return user;
    }

    public List<User> getFriendList(Integer userId){
        List<User> users = userStorage.findAll();
        User user = userStorage.get(userId);
        if(user.getFriends() == null){
            return null;
        }

        return users.stream()
                .filter(p -> p.getFriends().contains(Long.valueOf(userId)))
                .collect(Collectors.toList());
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }
}
