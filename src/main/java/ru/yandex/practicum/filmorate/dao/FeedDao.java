package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {
    void addToUserFeed(Integer userId, Integer entityId, EventType eventType, Operation operation);
    List<Feed> getUserFeed(Integer userId);
}
