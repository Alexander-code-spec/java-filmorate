package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class FeedDbStorage implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addToUserFeed(Integer userId, Integer entityId, FeedEventType eventType, FeedOperation operation) {
        // Вставим событие в таблицу feed
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id")
                .usingColumns(
                        "user_id",
                        "entity_id",
                        "event_type",
                        "operation"
                );
        simpleJdbcInsert.execute(
                Map.of(
                        "user_id", userId,
                        "entity_id", entityId,
                        "event_type", eventType.toString(),
                        "operation", operation.toString()
                )
        );
    }

    @Override
    public List<Feed> getUserFeed(Integer userId) {
        String sql = "SELECT\n" +
                "    event_id,\n" +
                "    user_id,\n" +
                "    entity_id,\n" +
                "    event_type,\n" +
                "    operation,\n" +
                "    timestamp\n" +
                "FROM feed\n" +
                "WHERE user_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapFeed(rs), userId);
    }

    private Feed mapFeed(ResultSet rs) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .eventType(FeedEventType.valueOf(rs.getString("event_type")))
                .operation(FeedOperation.valueOf(rs.getString("operation")))
                .timestamp(rs.getTimestamp("timestamp").getTime())
                .build();
    }
}
