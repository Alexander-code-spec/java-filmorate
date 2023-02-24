package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.Friendship;
import ru.yandex.practicum.filmorate.model.Friends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Qualifier("FriendshipDaoImplementation")
public class FriendshipDao implements Friendship {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void inviteFriend(Integer userId, Integer friendId) {
        String insertQuery = "insert into friendship (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(insertQuery, userId, friendId);
    }

    @Override
    public List<Friends> getAllFriends(Integer userId) {
        String sqlQuery = "select * from friendship where user_id = " + userId;
        log.info("Запрос на получение всех друзей.");
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFriend(rs));
    }

    @Override
    public void confirmFriendship(Integer userId, Integer friendId) {
        String sqlQuery = "update friendship set communication_status = ? " +
                "where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, true, userId, friendId);
    }

    @Override
    public void declineFriendship(Integer userId, Integer friendId) {
        String sqlQuery = "update friendship SET communication_status = ? " +
                "where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, false, userId, friendId);
    }

    @Override
    public void deleteFromFriends(Integer userId, Integer friendId) {
        String sql = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private Friends makeFriend(ResultSet friendRows) throws SQLException {
        return Friends.builder()
                .friendId(friendRows.getInt("friend_id"))
                .userId(friendRows.getInt("user_id"))
                .status(friendRows.getBoolean("communication_status")).build();
    }
}
