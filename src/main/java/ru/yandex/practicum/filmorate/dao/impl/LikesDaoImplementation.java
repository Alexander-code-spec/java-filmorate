package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.model.Likes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Qualifier("LikesDao")
public class LikesDaoImplementation implements LikesDao {
    private JdbcTemplate jdbcTemplate;

    public LikesDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createFilmLike(Integer filmId, Integer userId) {
        String sqlQuery = "insert into likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteFilmLike(Integer filmId, Integer userId) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Integer> getAllFilmLikes(Integer filmId) {
        String sqlQuery = "select * from likes where film_id = " + filmId;
        log.info("Выполняется запрос всех пользователей из базы");

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> createLikes(rs).getUser_id());
    }

    private Likes createLikes(ResultSet likeRows) throws SQLException {
        return Likes.builder()
                .film_id(likeRows.getInt("film_id"))
                .user_id((likeRows.getInt("user_id")))
                .build();
    }
}
