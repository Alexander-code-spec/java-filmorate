package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.Friendship;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final FilmsRecommendationService filmsRecommendationService;
    private UserStorage userStorage;
    private Friendship friendship;
    private LikesDao likesDao;
    private FilmStorage filmStorage;


    @Autowired
    public UserService(@Qualifier("UserDbStorage")UserStorage userStorage,
                       @Qualifier("FriendshipDaoImplementation")Friendship friendship,
                       @Qualifier("LikesDao")LikesDao likesDAO,
                       @Qualifier("FilmDbStorage")FilmStorage filmStorage){
        this.userStorage = userStorage;
        this.friendship = friendship;
        this.likesDao = likesDAO;
        this.filmStorage = filmStorage;
        filmsRecommendationService = new FilmsRecommendationService();
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
        userStorage.get(userId);
        for (Friends friend: friendship.getAllFriends(userId)){
            users.add(userStorage.get(friend.getFriendId()));
        }
        return users;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public List<Film> getFilmRecommendations(Integer userId) {
        Map <Integer, Map<Integer, Integer>> existingData = likesDao.findLikesForAllUsers();
        if (!existingData.containsKey(userId)) {
            return new ArrayList<>();
        }
        List<Integer> filmsIds =  filmsRecommendationService.recommendFilmsByUserId(existingData, userId);
        return filmStorage.getFilmsByIds(filmsIds);
    }
}
