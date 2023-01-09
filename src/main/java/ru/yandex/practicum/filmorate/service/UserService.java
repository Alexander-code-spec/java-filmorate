package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class UserService {
    private InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage){
        this.userStorage = userStorage;
    }

    public User addFriend(Integer userId, Integer friendId){
        User user = (User) userStorage.getMap().get(userId);
        User friend = (User) userStorage.getMap().get(friendId);

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
        User user = (User) userStorage.getMap().get(userId);
        User friend = (User) userStorage.getMap().get(friendId);

        if(user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь  с id = \"%s\" не найден!", userId));
        } else if(friend == null) {
            throw new ObjectNotFoundException(String.format("Друг с id = \"%s\" не найден!", friendId));
        }

        return userStorage.delete(user, friend);
    }

    public List<User> getFriendList(Integer userId){
        HashMap<Integer, User> users = userStorage.getMap();
        if(users.get(userId).getFriends() == null){
            return null;
        }

        return users.values().stream()
                .filter(p -> p.getFriends().contains(Long.valueOf(userId)))
                .collect(Collectors.toList());
    }
}
