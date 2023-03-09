package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friends;

import java.util.List;

public interface Friendship {
    void inviteFriend(Integer userId, Integer friendId);

    List<Friends> getAllFriends(Integer userId);

    void confirmFriendship(Integer userId, Integer friendId);

    void deleteFromFriends(Integer userId, Integer friendId);

    void declineFriendship(Integer userId, Integer friendId);
}
