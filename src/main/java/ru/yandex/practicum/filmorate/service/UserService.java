package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.Friendship;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;
    private Friendship friendship;

    @Autowired
    public UserService(@Qualifier("UserDbStorage")UserStorage userStorage,
                       @Qualifier("FriendshipDaoImplementation")Friendship friendship){
        this.userStorage = userStorage;
        this.friendship = friendship;
    }

    public User addFriend(Integer userId, Integer friendId){
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if(user == null) {
            throw new ObjectNotFoundException("Пользователь не найден!");
        } else if(friend == null) {
            throw new ObjectNotFoundException(String.format("Друг с id = %s не найден!", friendId));
        }
        friendship.inviteFriend(user.getId(), friend.getId());

        return user;
    }

    public User deleteFriend(Integer userId, Integer friendId){
        if(userId < 0) {
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        }
        friendship.deleteFromFriends(userId, friendId);

        return userStorage.get(userId);
    }

    public List<User> getFriendList(Integer userId){
        List<User> users = new ArrayList<>();

        for (Friends friend: friendship.getAllFriends(userId)){
            users.add(userStorage.get(friend.getFriendId()));
        }
        return users;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }
}
