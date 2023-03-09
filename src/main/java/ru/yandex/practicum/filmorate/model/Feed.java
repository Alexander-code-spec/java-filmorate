package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed {
    private long timestamp;
    private int userId;
    private FeedEventType eventType;
    private FeedOperation operation;
    private int eventId;
    private int entityId;
}
