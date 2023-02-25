package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.Friendship;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;

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
    private final FeedDao feedDao;

    @Autowired
    public UserService(@Qualifier("UserDbStorage")UserStorage userStorage,
                       @Qualifier("FriendshipDaoImplementation")Friendship friendship,
                       @Qualifier("LikesDao")LikesDao likesDAO,
                       @Qualifier("FilmDbStorage")FilmStorage filmStorage,
                       FeedDao feedDao){
        this.feedDao = feedDao;
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
        feedDao.addToUserFeed(user.getId(), friend.getId(), FeedEventType.FRIEND, FeedOperation.ADD);

        return user;
    }

    public User deleteFriend(Integer userId, Integer friendId){
        if(userId < 0) {
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        }
        friendship.deleteFromFriends(userId, friendId);
        feedDao.addToUserFeed(userId, friendId, FeedEventType.FRIEND, FeedOperation.REMOVE);

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

    public List<Feed> getUserFeed(Integer userId) {
        User user = userStorage.get(userId);

        return feedDao.getUserFeed(user.getId());
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
