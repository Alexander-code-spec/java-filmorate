package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {
    void addToUserFeed(Integer userId, Integer entityId, FeedEventType eventType, FeedOperation operation);
    List<Feed> getUserFeed(Integer userId);
}
