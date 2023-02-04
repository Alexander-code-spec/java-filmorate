package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Friendship {
    void inviteFriend(Integer userId, Integer friendId);

    List<Friends> getAllFriends(Integer userId);

    void confirmFriendship(Integer userId, Integer friendId);

    void deleteFromFriends(Integer userId, Integer friendId);

    void declineFriendship(Integer userId, Integer friendId);
}
